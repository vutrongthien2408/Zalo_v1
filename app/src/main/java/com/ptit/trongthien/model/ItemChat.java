package com.ptit.trongthien.model;

/**
 * Created by TrongThien on 4/18/2017.
 */
public class ItemChat {

    private String content;
    private String userNameSend;
    private String userNameRecieve;
    private String date;

    public ItemChat(String content, String userNameSend, String userNameRecieve, String date) {
        this.content = content;
        this.userNameSend = userNameSend;
        this.userNameRecieve = userNameRecieve;
        this.date = date;
    }

       public String getContent() {
        return content;
    }

    public String getUserNameSend() {
        return userNameSend;
    }

    public String getUserNameRecieve() {
        return userNameRecieve;
    }

    public String getDate() {
        return date;
    }
}
