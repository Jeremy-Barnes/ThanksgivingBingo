package com.bingo.bll;

import com.bingo.dal.HibernateUtil;
import com.bingo.dal.dto.entity.User;
import com.lambdaworks.codec.Base64;
import com.lambdaworks.crypto.SCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.resource.spi.InvalidPropertyException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

/**
 * Created by Jeremy on 8/9/2016.
 */
public class UserBLL {

	static final Logger logger = LoggerFactory.getLogger("application");

	public static List<User> searchUsers(String searchString){
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		List<User> users = entityManager
				.createQuery("from User where firstName like :searchTerm " +
									 "or lastName like :searchTerm or userName like :searchTerm " +
									 "or userName like :searchTerm " +
									 "and isActive = true")
				.setParameter("searchTerm", '%' + searchString + '%')
				.getResultList();
		entityManager.close();
		for(User user : users) {
			wipeSensitiveFields(user);
		}
		return users;
	}

	public static String createUserReturnUnHashedValidator(User user) throws Exception {
		user.setIsActive(true);
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		entityManager.getTransaction().begin();
		try {
			hashAndSaltPassword(user);
			String validatorUnHashed = createSelectorAndHashValidator(user);
			entityManager.persist(user);
			entityManager.getTransaction().commit();
			return validatorUnHashed;
		} catch(UnsupportedEncodingException us){
			logger.error("Couldn't create a password", us);
			throw new InvalidPropertyException("Sorry! We only accept passwords with characters in A-Z, a-z and 0-9.");
		} catch(Exception e) {
			logger.error("User creation failed for user " + user.toString(), e);
			return null;
		} finally {
			if(entityManager.getTransaction().isActive()){
				entityManager.getTransaction().rollback();
			}
			entityManager.close();
		}
	}

	public static User getUser(String selector, String validator) {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		try {
			User user = (User) entityManager.createQuery("from User where tokenSelector = :selector and isActive = true").setParameter("selector", selector).getSingleResult();

			if (verifyValidator(validator, user)) {
				return user;
			} else {
				return null;
			}
		} catch (PersistenceException ex) {
			logger.debug("Login failed for selector " + selector + "and validator " + validator, ex);
			return null;
		} finally {
			entityManager.close();
		}
	}

	public static User getUser(String userName, String password, boolean login) {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

		try {
			User user = (User) entityManager.createQuery("from User where userName = :userName and isActive = true").setParameter("userName", userName).getSingleResult();

			if (login) {
				entityManager.getTransaction().begin();
				String validator = null;
				if (checkLogin(user.getPassword(), password, user.getSalt())) {
					validator = createSelectorAndHashValidator(user);
					entityManager.getTransaction().commit();
				} else {
					return null;
				}
				if (validator != null) user.setTokenValidator(validator);
			} else {
				user = wipeSensitiveFields(user);
			}
			return user;
		} catch(NoResultException nrex) {//no user found
			logger.error("Login failed for " + userName, nrex);
			return null;
		} catch (Exception ex) {
			logger.error("A weird error occurred when logging in " + userName, ex);
			return null;
		} finally {
			if(entityManager.getTransaction().isActive()){
				entityManager.getTransaction().rollback();
			}
			entityManager.close();
		}
	}

	public static User getUser(int id) {
			User user = wipeSensitiveFields(getFullUser(id));
			return user;
	}

	public static List<User> searchForUser(String searchTerm) {
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("usersearch",User.class);

		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
		query.setParameter(1, "%" + searchTerm + "%");
		query.execute();
		List<User> results = query.getResultList();

		entityManager.close();
		for(User u : results){
			wipeSensitiveFields(u);
		}
		return results;
	}

	public static User updateUser(User changeUser, User sessionUser) throws Exception {

		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		try {
			entityManager.getTransaction().begin();

			if (changeUser.getPassword() != null && !changeUser.getPassword().isEmpty() && !changeUser.getPassword().equals(sessionUser.getPassword())) {
				hashAndSaltPassword(changeUser);
				sessionUser.setPassword(changeUser.getPassword());
				sessionUser.setSalt(changeUser.getSalt());
			}
			sessionUser.setFirstName(changeUser.getFirstName());
			sessionUser.setLastName(changeUser.getLastName());

			sessionUser.setIsActive(changeUser.getIsActive());
			entityManager.merge(sessionUser);
			entityManager.getTransaction().commit();
			return changeUser;
		} catch(UnsupportedEncodingException us) {
			logger.error("Couldn't create a password", us);
			throw new InvalidPropertyException("Sorry! We only accept passwords with characters in A-Z, a-z and 0-9.");
		} finally {
			if(entityManager.getTransaction().isActive()){
				entityManager.getTransaction().rollback();
			}
			entityManager.close();
		}
	}

	public static boolean deleteUser(User user) {
		try {
			user.setIsActive(false);
			updateUser(user, user);
		} catch (Exception e){
			logger.error("Delete user failed id:" + user.getUserID() + " uname: " + user.getUserID(), e);
			return false;
		}
		return true;
	}

	public static User wipeSensitiveFields(User user) {
		user.setSalt("");
		user.setPassword("");
		user.setTokenSelector("");
		user.setTokenValidator("");
		return user;
	}

	public static boolean isUserNameValid(String userName){
		boolean valid = true;
		valid = (userName != null && !userName.isEmpty()); //todo: content filter
		if(valid) {
			EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
			valid = !(boolean) entityManager.createNativeQuery("SELECT EXISTS(SELECT 1 from users where userName = ?1)")
											.setParameter(1, userName)
											.getSingleResult();
			entityManager.close();
		}

		return valid;
	}

	/***************** SECURITY STUFF **********************/
	private static void hashAndSaltPassword(User user) throws UnsupportedEncodingException {
		try {
			byte[] saltByte = new byte[16];
			SecureRandom.getInstance("SHA1PRNG").nextBytes(saltByte);
			String saltStr = new String(Base64.encode(saltByte));

			byte[] hashByte = SCrypt.scrypt(user.getPassword().getBytes("UTF-8"), saltStr.getBytes("UTF-8"), 16384, 8, 1, 64);
			String hashStr = new String(Base64.encode(hashByte));

			user.setPassword(hashStr);
			user.setSalt(saltStr);
		} catch (GeneralSecurityException ex) {
			logger.error("Could not run scrypt!", ex);
			System.exit(1); //if no secure algorithm is available, the service needs to shut down for emergency maintenance.
		}
	}

	private static String createSelectorAndHashValidator(User user) {
		String validatorStr = null;
		try {
			byte[] validatorByte = new byte[16];
			SecureRandom.getInstance("SHA1PRNG").nextBytes(validatorByte);
			validatorStr = new String(Base64.encode(validatorByte)); //Get in UTF
			byte[] hashedValidatorByte = SCrypt.scrypt(validatorStr.getBytes("UTF-8"), validatorStr.getBytes("UTF-8"), 16384, 8, 1, 64);

			user.setTokenSelector(UUID.randomUUID().toString());
			user.setTokenValidator(new String(Base64.encode(hashedValidatorByte)));
		} catch (GeneralSecurityException ex) {
			logger.error("Could not run scrypt!", ex);
			System.exit(1); //if no secure algorithm is available, the service needs to shut down for emergency maintenance.
		} catch (UnsupportedEncodingException ex) {
		} //shouldn't ever happen
		return validatorStr;
	}

	protected static boolean verifyValidator(String suppliedValidator, User dbUser) {
		String hashedCookieValidator = null;
		try {
			byte[] hashByte = SCrypt.scrypt(suppliedValidator.getBytes("UTF-8"), suppliedValidator.getBytes("UTF-8"), 16384, 8, 1, 64);
			hashedCookieValidator = new String(Base64.encode(hashByte));
		} catch (GeneralSecurityException ex) {
			logger.error("Could not run scrypt!", ex);
			System.exit(1); //if no secure algorithm is available, the service needs to shut down for emergency maintenance.
		} catch (UnsupportedEncodingException ex) {
			return false;
		}
		return hashedCookieValidator.equals(dbUser.getTokenValidator());
	}

	private static boolean checkLogin(String dbPasswordHash, String suppliedPassword, String suppliedSalt) {
		String hashStrConfirm = null;
		try {
			byte[] hashByte = SCrypt.scrypt(suppliedPassword.getBytes("UTF-8"), suppliedSalt.getBytes("UTF-8"), 16384, 8, 1, 64);
			hashStrConfirm = new String(Base64.encode(hashByte));
		} catch (GeneralSecurityException ex) {
			logger.error("Could not run scrypt!", ex);
			System.exit(1); //if no secure algorithm is available, the service needs to shut down for emergency maintenance.
		} catch (UnsupportedEncodingException ex) {
			return false;
		}
		return hashStrConfirm.equals(dbPasswordHash);
	}

	protected static User getFullUser(int id){
		EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
		try {
			User user = (User) entityManager.createQuery("from User where userID = :id and isActive = true").setParameter("id", id).getSingleResult();
			return user;
		} catch (PersistenceException ex) {
			logger.debug("No active user found with id " + id, ex);
			return null; //no user found
		} finally {
			entityManager.close();
		}
	}
}
