package com.sparshik.yogicapple.model;

/**
 * Data Structure for UserApplesStatus
 */
public class UserApplesStatus {
    private boolean offline;
    private boolean watched;
    private String localAudioFile;
    private String localVideoFile;

    public UserApplesStatus() {
    }

    public UserApplesStatus(boolean offline, String localAudioFile) {
        this.offline = offline;
        this.watched = false;
        this.localAudioFile = localAudioFile;
        this.localVideoFile = null;
    }

    public boolean isOffline() {
        return offline;
    }

    public boolean isWatched() {
        return watched;
    }

    public String getLocalAudioFile() {
        return localAudioFile;
    }

    public String getLocalVideoFile() {
        return localVideoFile;
    }
}
