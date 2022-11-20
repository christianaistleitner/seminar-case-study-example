package com.example.demo.models;

public class AckDto implements StreamEvent {

    private final String username;

    public AckDto(String username) {
        this.username = username;
    }

    @Override
    public Type getType() {
        return Type.MESSAGE;
    }

    public String getUsername() {
        return this.username;
    }
}
