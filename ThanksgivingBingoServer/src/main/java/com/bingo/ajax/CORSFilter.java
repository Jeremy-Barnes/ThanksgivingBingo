package com.bingo.ajax;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by Jeremy on 8/7/2016.
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext request,
					   ContainerResponseContext response) throws IOException {

		response.getHeaders().add("Access-Control-Allow-Origin", "http://0c7a5243.ngrok.io");
		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
		response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		response.getHeaders().add("Access-Control-Allow-Headers",  "origin, content-type, accept, authorization, selectorvalidator, Cookie, Content-Type, bingo, Content-Length");
		response.getHeaders().add("Access-Control-Expose-Headers", "SelectorValidator, JSESSIONID, bingo");
	}
}
