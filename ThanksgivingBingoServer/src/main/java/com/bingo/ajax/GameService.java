package com.bingo.ajax;

import com.bingo.dal.dto.AuthToken;
import com.bingo.dal.dto.entity.User;
import com.bingo.games.sockets.SocketManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Jeremy on 7/16/2017.
 */
@Path("/games")
public class GameService extends AjaxService {

	@GET
	@Path("/connectToMainGameServer/{clientID}")
	@Produces(MediaType.APPLICATION_JSON)
	public void connectToMainGameServer(@PathParam("clientID") String clientID, @Suspended final AsyncResponse asyncResponse) {
		SocketManager.tryToConnect(clientID, asyncResponse);
	}

	@GET
	@Path("/getAllActiveGames/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllActiveGames() {
		return Response.status(200).entity(SocketManager.findAllGames()).build();
	}


	@GET
	@Path("/getUsersGames/{userID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsersGames(@PathParam("gameType") int gameType, @PathParam("userID") int userID) {
		return null;
	}

	@GET
	@Path("/getSecureID")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSecureID(){
		User loggedInUser = (User) httpRequest.getSession().getAttribute("user");
		if(loggedInUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("You need to log in first!").build();
		}

		AuthToken auth = new AuthToken();
		auth.selector = SocketManager.setUserID(loggedInUser);
		return Response.status(Response.Status.OK).entity(auth).build();
	}
}
