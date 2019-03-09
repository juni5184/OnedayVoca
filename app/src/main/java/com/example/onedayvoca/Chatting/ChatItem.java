package com.example.onedayvoca.Chatting;

/**
 * Created by JUNI_DEV on 2018-07-11.
 */

public class ChatItem {
    private String name;
    private String content;

    public ChatItem() {
    }

    public ChatItem(String name, String content) {
        this.name = name;
        this.content= content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
