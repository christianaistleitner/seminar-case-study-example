package com.example.demo.models;

public class Ack {
    private final String username;
    private final String messageId;

    public Ack(String username, String messageId) {
        this.username = username;
        this.messageId = messageId;
    }

    public String getUsername() {
        return username;
    }

    public String getMessageId() {
        return messageId;
    }
}
