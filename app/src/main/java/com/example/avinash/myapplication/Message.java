package com.example.avinash.myapplication;
import java.util.Date;
public class Message {

    private String message, sentByEmail, sentByName, messageType, filePath;
    private long timestamp;

    public Message(String message, String sentByEmail, String sentByName, String messageType, String filePath, long timestamp) {
        this.message = message;
        this.sentByEmail = sentByEmail;
        this.sentByName = sentByName;
        this.messageType = messageType;
        this.filePath = filePath;
        this.timestamp = timestamp;
    }

    public Message(String message, String sentByEmail, String sentByName, String messageType, String filePath) {
        this.message = message;
        this.sentByEmail = sentByEmail;
        this.sentByName = sentByName;
        this.messageType = messageType;
        this.filePath = filePath;
        this.timestamp = new Date().getTime();
    }


    public Message(String message, String sentByEmail, String sentByName, String messageType) {
        this.message = message;
        this.sentByEmail = sentByEmail;
        this.sentByName = sentByName;
        this.messageType = messageType;
        this.timestamp = new Date().getTime();
        this.filePath = String.valueOf(this.timestamp);
    }

    public Message(String message, String sentByEmail, String sentByName, long timestamp) {
        this.message = message;
        this.sentByEmail = sentByEmail;
        this.sentByName = sentByName;
        this.timestamp = timestamp;
    }

    public Message(){

    }

    public Message(String message, String sentByEmail, String sentByName) {
        this.message = message;
        this.sentByEmail = sentByEmail;
        this.sentByName = sentByName;
        this.timestamp = new Date().getTime();
    }

    public String getSentByEmail() {
        return sentByEmail;
    }

    public void setSentByEmail(String sentByEmail) {
        this.sentByEmail = sentByEmail;
    }

    public String getSentByName() {
        return sentByName;
    }

    public void setSentByName(String sentByName) {
        this.sentByName = sentByName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
