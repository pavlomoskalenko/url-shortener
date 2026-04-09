package com.pavlomoskalenko.urlshortener.exception;

public class UsernameAlreadyExists extends RuntimeException {
    public UsernameAlreadyExists(String message) {
        super(message);
    }

    public UsernameAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }
}
