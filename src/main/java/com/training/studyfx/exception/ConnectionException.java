package com.training.studyfx.exception;

public class ConnectionException extends RuntimeException {
    public ConnectionException(String msg) {
        super(msg);
    }

    public ConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}