package com.pavlomoskalenko.urlshortener.exception;

public class ShortCodeAlreadyTakenException extends RuntimeException {
    public ShortCodeAlreadyTakenException(String message) {
        super(message);
    }

    public ShortCodeAlreadyTakenException(String message, Throwable cause) {
        super(message, cause);
    }
}
