package com.example.demo.models;

public class ChatMessage implements StreamEvent {

    private final String message;

    public ChatMessage(String message) {
        this.message = message;
    }

    @Override
    public Type getType() {
        return Type.MESSAGE;
    }

    public String getMessage() {
        return this.message;
    }
}
