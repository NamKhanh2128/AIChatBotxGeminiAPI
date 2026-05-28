package com.training.studyfx.model;

public class Message {
    private String sender;
    private String content;
    private String time;
    private boolean isBot;

    public Message() {
    }

    public Message(String sender, String content, String time) {
        this.sender = sender;
        this.content = content;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String v) {
        this.sender = v;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String v) {
        this.content = v;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String v) {
        this.time = v;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean v) {
        this.isBot = v;
    }
}