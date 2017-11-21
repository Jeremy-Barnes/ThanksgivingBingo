package com.bingo.dal.dto.entity;

import com.bingo.dal.dto.DTO;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeremy on 8/9/2016.
 */
@Entity
@Table(name="users")
public class User extends DTO {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private int userID;
	private String userName;
	private String firstName;
	private String lastName;
	private String password;
	private String salt;
	private String tokenSelector;
	private String tokenValidator;
	private boolean isActive;

	public User(int userID, String userName, String firstName, String lastName, String password,
				String salt, String tokenSelector, String tokenValidator, boolean isActive) {
		this.userID = userID;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.salt = salt;
		this.tokenSelector = tokenSelector;
		this.tokenValidator = tokenValidator;
		this.isActive = isActive;
	}

	public User(User copyUser) {
		this.userID = copyUser.userID;
		this.userName = copyUser.userName;
		this.firstName = copyUser.firstName;
		this.lastName = copyUser.lastName;
		this.password = copyUser.password;//todo should this really be copied?
		this.salt = copyUser.salt;//todo should this really be copied?
		this.tokenSelector = copyUser.tokenSelector; //todo should this really be copied?
		this.tokenValidator = copyUser.tokenValidator;//todo should this really be copied?
		this.isActive = copyUser.isActive;
	}

	public User(){}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getTokenSelector() {
		return tokenSelector;
	}

	public void setTokenSelector(String tokenSelector) {
		this.tokenSelector = tokenSelector;
	}

	public String getTokenValidator() {
		return tokenValidator;
	}

	public void setTokenValidator(String tokenValidator) {
		this.tokenValidator = tokenValidator;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void nullAllButID(){
		this.userName = null;
		this.firstName = null;
		this.lastName = null;
		this.password = null;
		this.salt = null;
		this.tokenSelector = null;
		this.tokenValidator = null;
		this.isActive = true;
	}
}
