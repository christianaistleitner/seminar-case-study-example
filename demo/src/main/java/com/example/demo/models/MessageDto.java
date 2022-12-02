package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDto extends PacketDto {

    private final String id = UUID.randomUUID().toString();

    private final String text;

    private final String author;

    private final LocalDateTime sentTimestamp = LocalDateTime.now();

    public MessageDto(String text, String author, String recipient) {
        super(recipient);
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

    @Override
    public Type getType() {
        return Type.MSG;
    }
}
