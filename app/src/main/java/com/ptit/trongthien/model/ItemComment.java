package com.ptit.trongthien.model;

/**
 * Created by TrongThien on 6/24/2017.
 */
public class ItemComment {
    private int id;
    private int idStatus;
    private String name;
    private String comment;
    private String avatarUserCmt;

    public ItemComment(int id, int idStatus, String name, String comment,String avatarUserCmt ) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.idStatus = idStatus;
        this.avatarUserCmt = avatarUserCmt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public String getAvatarUserCmt() {
        return avatarUserCmt;
    }
}
