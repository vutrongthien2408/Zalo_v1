package com.ptit.trongthien.model;

/**
 * Created by TrongThien on 5/29/2017.
 */
public class LikeStatus {
    private int id;
    private int idStatus;
    private String userName;

    public LikeStatus(int id, int idStatus, String userName) {
        this.id = id;
        this.idStatus = idStatus;
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public String getUserName() {
        return userName;
    }
}
