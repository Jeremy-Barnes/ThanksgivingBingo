package com.bingo.ajax;

import com.bingo.bll.UserBLL;
import com.bingo.dal.dto.AuthToken;
import com.bingo.dal.dto.entity.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;


/**
 * Created by Jeremy on 8/7/2016.
 */
@Path("/users")
public class UserService extends AjaxService{

	@POST
	@Path("/createUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(JAXBElement<User> jsonRequest){
		User request = jsonRequest.getValue();

		boolean userNameAvailable = true;

		userNameAvailable = UserBLL.isUserNameValid(request.getUserName());


		if(!userNameAvailable){
			return Response.status(Response.Status.CONFLICT).entity("Sorry! This user name is not available! Try a different one.").build();
		}

		try {
			String validator = UserBLL.createUserReturnUnHashedValidator(request);
			if (validator != null) {
				httpRequest.getSession().setAttribute("user", request);
				User copiedUser = super.serializeDeepCopy(request, User.class);
				return Response.status(Response.Status.OK).cookie(createUserCookies(copiedUser))
							   .entity(UserBLL.wipeSensitiveFields(copiedUser)).build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("User creation failed for unknown reason.").build();
			}
		} catch(Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path("/getUserFromLogin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserFromLogin(JAXBElement<User> jsonUser) {
		User user = jsonUser.getValue();
		user = UserBLL.getUser(user.getUserName(), user.getPassword(), true);

		httpRequest.getSession().setAttribute("user", user);
		if(user == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid email/password. Try again!").build();
		}
		User copiedUser = super.serializeDeepCopy(user, User.class);

		return Response.status(Response.Status.OK)
					   .cookie(createUserCookies(copiedUser))
					   .header("JSESSIONID", httpRequest.getSession().getId())
					   .entity(UserBLL.wipeSensitiveFields(copiedUser)).build();
	}

	@POST
	@Path("/getUserFromToken")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserFromToken(JAXBElement<AuthToken> jsonToken) {
		AuthToken token = jsonToken.getValue();

		User user = UserBLL.getUser(token.selector, token.validator);
		httpRequest.getSession().setAttribute("user", user);
		User copiedUser = super.serializeDeepCopy(user, User.class);

		return Response.status(Response.Status.OK)
					   .entity(UserBLL.wipeSensitiveFields(copiedUser)).build();
	}

	@DELETE
	@Path("/deleteUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUserAccount() {
		User loggedInUser = (User) httpRequest.getSession().getAttribute("user");

		if(loggedInUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("You need to log in first!").build();
		} else {
			return UserBLL.deleteUser(loggedInUser) ? Response.status(Response.Status.OK).build() :
					Response.status(Response.Status.BAD_REQUEST).entity("Couldn't delete your account. You're stuck with us! Contact an admin for help.").build();
		}
	}


	@GET
	@Path("/getUserFromID/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserFromID(@PathParam("id") int id) {
		User user = UserBLL.getUser(id);
		if(user == null){
			return Response.status(Response.Status.NOT_FOUND).entity("No such user exists").build();
		}
		return Response.status(Response.Status.OK).entity(user).build();
	}
}
