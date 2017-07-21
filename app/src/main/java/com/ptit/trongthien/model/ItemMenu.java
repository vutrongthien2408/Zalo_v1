package com.ptit.trongthien.model;

/**
 * Created by TrongThien on 7/6/2017.
 */
public class ItemMenu {
    private int menu;
    private int img;
    private int background;

    public ItemMenu(int menu, int img, int background) {
        this.menu = menu;
        this.img = img;
        this.background = background;
    }

    public int getMenu() {
        return menu;
    }

    public int getImg() {
        return img;
    }

    public int getBackground() {
        return background;
    }
}
