package com.example.demo.database.models;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
public class Message {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String username;

    @Column
    private String text;

    @Column
    private OffsetDateTime dateSent;

    public Message() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getDateSent() {
        return dateSent;
    }

    public void setDateSent(OffsetDateTime dateSent) {
        this.dateSent = dateSent;
    }
}
