package com.sparshik.yogicapple.model;

import java.util.HashMap;

/**
 * Defines data structure for a meditation tree apple
 */
public class PackApple {
    private int appleSeqNumber;
    private String appleTitle;
    private String audioURL;
    private String videoUrl;
    private String appleGuideSiteUrl;
    private int appleDuration;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;


    public PackApple() {
    }

    public PackApple(int appleSeqNumber, String appleTitle, String audioURL,
                     String videoUrl, int appleDuration, String appleGuideSiteUrl, HashMap<String, Object> timestampCreated) {
        this.appleSeqNumber = appleSeqNumber;
        this.appleTitle = appleTitle;
        this.audioURL = audioURL;
        this.videoUrl = videoUrl;
        this.appleDuration = appleDuration;
        this.appleGuideSiteUrl = appleGuideSiteUrl;
        this.timestampCreated = timestampCreated;
        this.timestampLastChanged = null;
    }

    public String getAppleGuideSiteUrl() {
        return appleGuideSiteUrl;
    }

    public int getAppleSeqNumber() {
        return appleSeqNumber;
    }

    public String getAppleTitle() {
        return appleTitle;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getAppleDuration() {
        return appleDuration;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }
}
