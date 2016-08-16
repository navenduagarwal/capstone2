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
    private String defaultPackId;
    private String defaultProgramId;

    public User() {
    }

    public User(String email, String name, boolean verified, String defaultProgramId, String defaultPackId, HashMap<String, Object> timestampJoined) {
        this.email = email;
        this.name = name;
        this.timestampJoined = timestampJoined;
        this.verified = verified;
        this.defaultPackId = defaultPackId;
        this.defaultProgramId = defaultProgramId;
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

    public String getDefaultPackId() {
        return defaultPackId;
    }

    public String getDefaultProgramId() {
        return defaultProgramId;
    }
}
