package com.example.demo.models;

public class AckDto extends PacketDto {

    private final String username;
    private final String messageId;

    public AckDto(String username, String messageId) {
        super(username);
        this.username = username;
        this.messageId = messageId;
    }

    @Override
    public Type getType() {
        return Type.ACK;
    }

    public String getUsername() {
        return this.username;
    }

    public String getMessageId() {
        return messageId;
    }
}
