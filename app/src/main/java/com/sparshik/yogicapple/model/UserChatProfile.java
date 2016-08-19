package com.sparshik.yogicapple.model;


/**
 * data structure for user chat profile
 */
public class UserChatProfile {
    private String nickName;
    private String chatProfilePicUrl;

    public UserChatProfile() {
    }

    public UserChatProfile(String nickName, String chatProfilePicUrl) {
        this.nickName = nickName;
        this.chatProfilePicUrl = chatProfilePicUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public String getChatProfilePicUrl() {
        return chatProfilePicUrl;
    }
}
