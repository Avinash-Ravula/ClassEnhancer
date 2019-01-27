package com.example.avinash.myapplication;

public class Chat {

    private String chatName,lastMessage;
    private long lastTimestamp;

    public Chat(String chatName, String lastMessage, long lastTimestamp) {
        this.chatName = chatName;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }
}
