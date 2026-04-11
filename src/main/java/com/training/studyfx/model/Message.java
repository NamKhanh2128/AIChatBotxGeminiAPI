package com.training.studyfx.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content;
    private String sender;
    private LocalDateTime timestamp;
    private boolean isFromBot;

    public Message(String content, String sender, boolean isFromBot) {
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
        this.isFromBot = isFromBot;
    }

    // Getters
    public String getContent() {
        return content;
    }

}