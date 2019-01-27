package com.example.avinash.myapplication.app;

public class ChatItem {
    private String chatName,chatLastMessage;
    private long timestamp;

    public ChatItem(String chatName, String chatLastMessage, long timestamp) {
        this.chatName = chatName;
        this.chatLastMessage = chatLastMessage;
        this.timestamp = timestamp;
    }

    public String getChatName() {
        return this.chatName;
    }

    public String getChatLastMessage() {
        return this.chatLastMessage;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
