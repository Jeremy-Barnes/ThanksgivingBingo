package com.bingo.ajax;

import com.bingo.bll.UserBLL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;

/**
 * Created by Jeremy on 8/22/2016.
 */
@Path("/meta")
public class MetaService extends AjaxService {

	@Path("/status")
	@GET
	@Produces("text/plain")
	public Response checkJobs() throws FileNotFoundException {
		return Response.status(Response.Status.OK).entity("Healthy").build();
	}

	@GET
	@Path("/checkUserName/{userName}")
	public boolean checkUserNameAvailability(@PathParam("userName") String userName) {
		return UserBLL.isUserNameValid(userName);
	}


}
