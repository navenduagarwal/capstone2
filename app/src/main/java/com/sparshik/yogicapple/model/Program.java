package com.sparshik.yogicapple.model;

import android.graphics.Color;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Defines data structure for a meditation program
 */
public class Program {
    private String programTitle;
    private String programBuiltBy;
    private String programDesc;
    private String programIconUrl;
    private String programBannerUrl;
    private String programDevSiteUrl;
    private String programColor;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;


    public Program() {
    }

    public Program(String programTitle, String programBuiltBy, String programDesc, String programColor,
                   String programIconUrl, String programBannerUrl, String programDevSiteUrl, HashMap<String, Object> timestampCreated) {
        this.programTitle = programTitle;
        this.programBuiltBy = programBuiltBy;
        this.programDesc = programDesc;
        this.programColor = programColor;
        this.programIconUrl = programIconUrl;
        this.programBannerUrl = programBannerUrl;
        this.timestampCreated = timestampCreated;
        this.programDevSiteUrl = programDevSiteUrl;
        this.timestampLastChanged = null;
    }

    public String getProgramDevSiteUrl() {
        return programDevSiteUrl;
    }

    public String getProgramBannerUrl() {
        return programBannerUrl;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public String getProgramDesc() {
        return programDesc;
    }

    public String getProgramIconUrl() {
        return programIconUrl;
    }

    public String getProgramBuiltBy() {
        return programBuiltBy;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public String getProgramColor() {
        return programColor;
    }

    @Exclude
    public int getProgramColorInt() {
        return Color.parseColor(programColor);
    }

}