package com.sparshik.yogicapple.model;


/**
 * data structure for user chat profile
 */
public class UserChatProfile {
    private String nickName;
    private int chatImageResId;

    public UserChatProfile() {
    }

    public UserChatProfile(String nickName, int chatImageResId) {
        this.nickName = nickName;
        this.chatImageResId = chatImageResId;
    }

    public String getNickName() {
        return nickName;
    }

    public int getChatImageResId() {
        return chatImageResId;
    }
}
