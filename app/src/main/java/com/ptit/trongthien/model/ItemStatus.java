package com.ptit.trongthien.model;

/**
 * Created by TrongThien on 4/22/2017.
 */
public class ItemStatus {
    private int id;
    private String userName;
    private String postDate;
    private String status;
    private String avatar;
    private String imageStatus;
    private int likeStatus;
    private String modeStatus;

    public ItemStatus(int id, String userName, String postDate, String status, String avatar, String imageStatus, int likeStatus) {
        this.userName = userName;
        this.postDate = postDate;
        this.status = status;
        this.avatar = avatar;
        this.imageStatus = imageStatus;
        this.id = id;
        this.likeStatus = likeStatus;
    }

    public ItemStatus(int id, String userName, String postDate, String status, String avatar, String imageStatus, int likeStatus, String modeStatus) {
        this.id = id;
        this.userName = userName;
        this.postDate = postDate;
        this.status = status;
        this.avatar = avatar;
        this.imageStatus = imageStatus;
        this.likeStatus = likeStatus;
        this.modeStatus = modeStatus;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPostDate() {
        return postDate;
    }

    public String getStatus() {
        return status;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getImageStatus() {
        return imageStatus;
    }

    public String getModeStatus() {
        return modeStatus;
    }
}
