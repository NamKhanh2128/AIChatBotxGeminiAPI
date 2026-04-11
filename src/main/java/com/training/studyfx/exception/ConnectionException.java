package com.training.studyfx.exception;

public class ConnectionException extends RuntimeException {
    public ConnectionException(String message) {
        super(message);
    }
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
} 