package com.sparshik.yogicapple.model;

import java.util.HashMap;

/**
 * data structure for group chat
 */
public class ChatMessage {
    private String text;
    private String encodedEmail;
    private boolean flagged;
    private HashMap<String, Object> timestampCreated;

    public ChatMessage() {
    }

    public ChatMessage(String text, String encodedEmail, boolean flagged, HashMap<String, Object> timestampCreated) {
        this.text = text;
        this.encodedEmail = encodedEmail;
        this.flagged = flagged;
        this.timestampCreated = timestampCreated;
    }

    public String getText() {
        return text;
    }

    public String getEncodedEmail() {
        return encodedEmail;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }
}
