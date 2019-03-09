package com.example.onedayvoca.Chatting;

public class ChatVO {

    private int imageID ;
    private String user_id;
    private String receiver;
    private String content;
    private String time;
    private int type;

    public ChatVO(){}

    public ChatVO( String user_id, String content, String time) {
        this.user_id = user_id;
        this.content = content;
        this.time = time;
    }

    public ChatVO(int imageID, String user_id, String content, String time,int type) {
        this.imageID = imageID;
        this.user_id = user_id;
        this.content = content;
        this.time = time;
        this.type= type;
    }

    public int getImageID() {
        return imageID;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }


    public int getType() {
        return type;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public void setType(int type) {
        this.type = type;
    }
}
