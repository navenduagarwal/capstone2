package com.sparshik.yogicapple.model;

import android.graphics.Color;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Defines data structure for a meditation pack
 */
public class Pack {
    private String packTitle;
    private String packShortDesc;
    private String packDesc;
    private String packColor;
    private String packIconUrl;
    private String packBannerUrl;
    private String packDevSiteUrl;
    private int packDuration;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;

    public Pack() {
    }

    public Pack(String packTitle, String packShortDesc, String packDesc, String packColor, String packIconUrl,
                String packBannerUrl, String packDevSiteUrl, HashMap<String, Object> timestampCreated) {
        this.packTitle = packTitle;
        this.packShortDesc = packShortDesc;
        this.packDesc = packDesc;
        this.packColor = packColor;
        this.packIconUrl = packIconUrl;
        this.packBannerUrl = packBannerUrl;
        this.packDevSiteUrl = packDevSiteUrl;
        this.packDuration = 0;
        this.timestampCreated = timestampCreated;
        this.timestampLastChanged = null;
    }

    public String getPackDevSiteUrl() {
        return packDevSiteUrl;
    }

    public String getPackBannerUrl() {
        return packBannerUrl;
    }

    public String getPackTitle() {
        return packTitle;
    }

    public String getPackDesc() {
        return packDesc;
    }

    public String getPackIconUrl() {
        return packIconUrl;
    }

    public String getPackShortDesc() {
        return packShortDesc;
    }


    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public int getPackDuration() {
        return packDuration;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public String getPackColor() {
        return packColor;
    }

    @Exclude
    public int getPackColorInt() {
        return Color.parseColor(packColor);
    }
}
