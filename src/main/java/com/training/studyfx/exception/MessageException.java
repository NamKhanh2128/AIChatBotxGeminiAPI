package com.training.studyfx.exception;

public class MessageException extends RuntimeException {
    public MessageException(String msg) {
        super(msg);
    }

    public MessageException(String msg, Throwable cause) {
        super(msg, cause);
    }
}