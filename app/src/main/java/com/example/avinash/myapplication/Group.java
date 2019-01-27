package com.example.avinash.myapplication;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class Group {
    private String title,lastMessage,created_by;
    private long timestamp;

    public Group(String title, String lastMessage)
    {
        this.title = title;
        this.lastMessage = lastMessage;
        created_by = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        timestamp = new Date().getTime();
    }

    public Group(String title, String lastMessage, String created_by, long timestamp) {
        this.title = title;
        this.lastMessage = lastMessage;
        this.created_by = created_by;
        this.timestamp = timestamp;
    }

    public Group(){

    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
