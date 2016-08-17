package com.sparshik.yogicapple.model;

/**
 * Data Structure for UserOfflineDownloads
 */
public class UserOfflineDownloads {
    private String localAudioFile;
    private String localVideoFile;

    public UserOfflineDownloads() {
    }

    public UserOfflineDownloads(String localAudioFile) {
        this.localAudioFile = localAudioFile;
        this.localVideoFile = null;
    }


    public String getLocalAudioFile() {
        return localAudioFile;
    }

    public String getLocalVideoFile() {
        return localVideoFile;
    }
}
