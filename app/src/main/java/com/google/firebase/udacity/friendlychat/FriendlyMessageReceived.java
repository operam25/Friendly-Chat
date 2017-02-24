package com.google.firebase.udacity.friendlychat;

/**
 * Created by khandelwal on 08/02/17.
 */

public class FriendlyMessageReceived {

    private String text;
    private String name;
    private String receiverName;
    private String photoUrl;
    private Long time;
    private String status;

    public FriendlyMessageReceived() {
    }

    public FriendlyMessageReceived(String text, String name, String photoUrl, String receiverName, Long time, String status) {
        this.text = text;
        this.name = name;
        this.receiverName = receiverName;
        this.photoUrl = photoUrl;
        this.time = time;
        this.status = status;
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

}
