package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {

    private final String id = UUID.randomUUID().toString();

    private final String text;

    private final String author;

    private final LocalDateTime sentTimestamp = LocalDateTime.now();

    public Message(String text, String author) {
        this.text = text;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return this.text;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getSentTimestamp() {
        return sentTimestamp;
    }
}
