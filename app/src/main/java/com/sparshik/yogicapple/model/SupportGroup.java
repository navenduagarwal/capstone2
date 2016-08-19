package com.sparshik.yogicapple.model;

import java.util.HashMap;

/**
 * Defines data structure for a meditation pack
 */
public class SupportGroup {
    private String groupName;
    private String groupDescription;
    private boolean active;
    private String groupImageUrl;
    private int memberCount;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;

    public SupportGroup() {
    }

    public SupportGroup(String groupName, String groupDescription, String groupImageUrl, HashMap<String, Object> timestampCreated) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.active = true;
        this.groupImageUrl = groupImageUrl;
        this.memberCount = 1;
        this.timestampCreated = timestampCreated;
        this.timestampLastChanged = null;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public boolean isActive() {
        return active;
    }

    public String getGroupImageUrl() {
        return groupImageUrl;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }
}
