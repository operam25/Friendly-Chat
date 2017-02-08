package com.google.firebase.udacity.friendlychat;

/**
 * Created by khandelwal on 08/02/17.
 */

public class UserList {

    private String name;
    private String email;

    public UserList() {
    }

    public UserList(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

}
