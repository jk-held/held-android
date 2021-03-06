package com.held.retrofit.response;


public class FeedData {

    private String date;
    private String image;
    private long held;
    private String text;
    private String rid;
    private String owner_display_name;
    private String owner_pic;
    private String thumbnail;

    public String getThumbnail() {
        return thumbnail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getHeld() {
        return held;
    }

    public void setHeld(int held) {
        this.held = held;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getOwner_display_name() {
        return owner_display_name;
    }

    public String getOwner_pic() {
        return owner_pic;
    }

    public void setOwner_display_name(String owner_display_name) {
        this.owner_display_name = owner_display_name;
    }
}
