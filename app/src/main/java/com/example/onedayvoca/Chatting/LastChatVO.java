package com.example.onedayvoca.Chatting;

public class LastChatVO {

    private String user_id;
    private String receiver;
    private String content;
    private String time;

    public LastChatVO(){}

    public LastChatVO( String user_id, String content, String time) {
        this.user_id = user_id;
        this.content = content;
        this.time = time;
    }


    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
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

}
