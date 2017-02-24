package com.google.firebase.udacity.friendlychat;

import com.google.firebase.database.ServerValue;

import java.util.Map;

public class FriendlyMessage {

    private String text;
    private String name;
    private String receiverName;
    private String photoUrl;
    private Map time;
    private String status;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl, String receiverName) {
        this.text = text;
        this.name = name;
        this.receiverName = receiverName;
        this.photoUrl = photoUrl;
        this.time = ServerValue.TIMESTAMP;
        this.status = "send";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getReceiverName(){
        return receiverName;
    }

    public void setReceiverName(String receiverName){
        this.receiverName = receiverName;
    }

    public Map getTime() {
        return time;
    }

    public void setTime(Map time) {
        this.time = time;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

}
