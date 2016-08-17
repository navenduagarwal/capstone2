package com.sparshik.yogicapple.model;

import java.util.HashMap;

/**
 * Defines the data structure of user objects
 */
public class User {
    private String email;
    private String name;
    private HashMap<String, Object> timestampJoined;
    private boolean verified;
    private String currentPackId;
    private String currentProgramId;

    public User() {
    }

    public User(String email, String name, boolean verified, String currentProgramId, String currentPackId, HashMap<String, Object> timestampJoined) {
        this.email = email;
        this.name = name;
        this.timestampJoined = timestampJoined;
        this.verified = verified;
        this.currentPackId = currentPackId;
        this.currentProgramId = currentProgramId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getCurrentPackId() {
        return currentPackId;
    }

    public String getCurrentProgramId() {
        return currentProgramId;
    }
}
