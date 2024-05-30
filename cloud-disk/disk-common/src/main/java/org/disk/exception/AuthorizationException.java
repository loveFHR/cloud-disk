package org.disk.exception;

public class AuthorizationException extends RuntimeException{
    public AuthorizationException(String msg) {
        super(msg);
    }
}
