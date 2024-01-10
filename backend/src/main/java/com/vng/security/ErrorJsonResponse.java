package com.vng.security;

public class ErrorJsonResponse {
    public boolean isError = true;
    public boolean shouldLogin = false;
    public String errorMessage;

    public ErrorJsonResponse(String error) {
        this.errorMessage = error;
        this.shouldLogin = false;
    }
}
