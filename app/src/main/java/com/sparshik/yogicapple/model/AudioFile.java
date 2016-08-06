package com.sparshik.yogicapple.model;

import java.util.HashMap;

/**
 * Model for AudioFile Files
 */
public class AudioFile {
    private int order;
    private boolean publish;
    private String storageURL;
    private HashMap<String, Object> timestampCreated;


    public AudioFile() {
    }

    public AudioFile(int order, boolean publish, String storageURL, HashMap<String, Object> timestampCreated) {
        this.order = order;
        this.publish = publish;
        this.storageURL = storageURL;
        this.timestampCreated = timestampCreated;
    }

    public int getOrder() {
        return order;
    }

    public boolean isPublish() {
        return publish;
    }

    public String getStorageURL() {
        return storageURL;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }
}
