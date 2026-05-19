package com.training.studyfx.exception;

public class ChatException extends RuntimeException {
    public ChatException(String msg) {
        super(msg);
    }

    public ChatException(String msg, Throwable cause) {
        super(msg, cause);
    }
}