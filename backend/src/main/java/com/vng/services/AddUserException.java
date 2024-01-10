package com.vng.services;

import javax.ws.rs.core.Response;

@SuppressWarnings("serial")
public class AddUserException extends Exception {
    public AddUserException(Response response) {
        super("response " + response.getStatus() + ": " + response.readEntity(String.class));
    }

    public AddUserException(String message) {
        super(message);
    }

}
