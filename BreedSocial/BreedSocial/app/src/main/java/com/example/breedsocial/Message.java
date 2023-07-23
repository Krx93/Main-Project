package com.example.breedsocial;

import com.google.firebase.Timestamp;
import java.util.Date;

public class Message {
    private String sender_id;
    private String content;
    private Timestamp timestamp;
    private String name;
    private String image_url;

    public Message() {}

    public Message(String sender_id, String content, Timestamp timestamp, String name, String image_url) {
        this.sender_id = sender_id;
        this.content = content;
        this.timestamp = timestamp;
        this.name = name;
        this.image_url = image_url;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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
}





