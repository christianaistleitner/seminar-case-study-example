package com.example.demo.models;

public interface StreamEvent {

    Type getType();

    public static enum Type {
        MESSAGE;
    }
}
