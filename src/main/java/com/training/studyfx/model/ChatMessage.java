package com.training.studyfx.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage {

    public enum MessageType {
        USER, BOT, SYSTEM
    }

    private String text;
    private MessageType type;
    private String time;
    private String sender;

    public ChatMessage(String text, MessageType type, String time) {
        this.text = text;
        this.type = type;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String v) {
        this.text = v;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType v) {
        this.type = v;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String v) {
        this.time = v;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String v) {
        this.sender = v;
    }

    public static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}