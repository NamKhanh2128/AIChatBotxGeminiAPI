package com.training.studyfx.model;

public class ChatMessage {

    public enum MessageType {
        USER,
        BOT,
        TYPING
    }

    private String text;
    private MessageType type;
    private String timestamp;  // Added timestamp field

    public ChatMessage(String text, MessageType type, String timestamp) {
        this.text = text;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

}