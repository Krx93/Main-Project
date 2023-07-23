package com.example.breedsocial;

import java.util.Map;

public class Conversation {
    private Map<String, Boolean> users;

    private String content;
    private String conversation_id;
    private String name;
    private String image_url;
    private String text;
    private long timestamp;
    private String sender_id;
    private String other_id;

    public Conversation(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public Conversation() {
        // Empty constructor needed for Firestore
    }

    public Conversation(Map<String, Boolean> users) {
        this.users = users;
    }

    public Map<String, Boolean> getUsers() {
        return users;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getOther_id() {
        return other_id;
    }

    public void setOther_id(String other_id) {
        this.other_id = other_id;
    }
}
