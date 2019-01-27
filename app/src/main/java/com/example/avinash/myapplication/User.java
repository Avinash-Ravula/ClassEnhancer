package com.example.avinash.myapplication;

import java.util.Date;

public class User {
    private String name, email;
    private long timestamp;

    public User(String name,String email){
        this.name = name;
        this.email = email;
        timestamp = new Date().getTime();
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
