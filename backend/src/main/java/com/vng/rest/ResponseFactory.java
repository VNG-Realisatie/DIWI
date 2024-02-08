package com.vng.rest;

import com.vng.models.ErrorResponse;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class ResponseFactory {

	// 200
	public static Response jsonSuccesResponse(Object data) {
        return Response.status(Response.Status.OK)
                .entity(data)
                .type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	// 400
	public static Response jsonClientErrorResponse(Object data) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(data)
                .type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	// 404
	public static Response jsonNotFoundResponse() {
        return ResponseFactory.jsonNotFoundResponse(new ErrorResponse("The requested resource could not be found"));
	}

	public static Response jsonNotFoundResponse(Object data) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(data)
                .type(MediaType.APPLICATION_JSON_TYPE).build();		
	}
	
	// 500
	public static Response jsonServerErrorResponse(Object data) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(data)
                .type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}
