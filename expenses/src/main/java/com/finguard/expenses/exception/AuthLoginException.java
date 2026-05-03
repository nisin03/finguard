package com.finguard.expenses.exception;

public class AuthLoginException extends RuntimeException {

    public AuthLoginException() {
    }

    public AuthLoginException(String message) {
        super(message);
    }

    public AuthLoginException(String message, Throwable cause) {
        super(message, cause);
    }

}
