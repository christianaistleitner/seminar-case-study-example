package com.example.demo.models;

public abstract class PacketDto {

    private final String recipient;

    public PacketDto(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public abstract Type getType();

    public enum Type {
        MSG,
        ACK;
    }
}
