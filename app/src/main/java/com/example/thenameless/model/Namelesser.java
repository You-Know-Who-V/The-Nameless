package com.example.thenameless.model;

import android.app.Application;

public class Namelesser extends Application {

    private String userName;
    private String userId;

    public static Namelesser instance;

    public Namelesser(){

    }

    public static Namelesser getInstance(){
        if(instance == null){
            instance = new Namelesser();
        }
        return instance;
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
