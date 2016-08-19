package com.sparshik.yogicapple.model;

import java.util.HashMap;

/**
 * data structure for group chat
 */
public class ChatMessage {
    private String text;
    private String nickName;
    private String userProfilePicUrl;
    private boolean flagged;
    private HashMap<String, Object> timestampCreated;

    public ChatMessage() {
    }

    public ChatMessage(String text, String nickName, String userProfilePicUrl, boolean flagged, HashMap<String, Object> timestampCreated) {
        this.text = text;
        this.nickName = nickName;
        this.userProfilePicUrl = userProfilePicUrl;
        this.flagged = flagged;
        this.timestampCreated = timestampCreated;
    }

    public String getText() {
        return text;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }
}
