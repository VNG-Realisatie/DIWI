package com.vng.security;

@SuppressWarnings("serial")
public class MailException extends Exception {
    public MailException(Exception ex) {
        super (ex);
    }

}
