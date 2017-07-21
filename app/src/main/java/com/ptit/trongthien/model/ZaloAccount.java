package com.ptit.trongthien.model;

/**
 * Created by TrongThien on 4/17/2017.
 */
public class ZaloAccount {
    private String userName;
    private String password;
    private String phoneNumber;
    private String avatar;

    public ZaloAccount(String userName, String password, String phoneNumber, String avatar) {
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
    }


    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }
}
