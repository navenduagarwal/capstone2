package com.sparshik.yogicapple.model;

import java.util.HashMap;

/**
 * Data Structure for an event
 */
public class Event {
    private String title;
    private long startDateInMills;
    private long endDateInMills;
    private String eventOrganiser;
    private String eventDesc;
    private String eventUrl;
    private String eventImageUrl;
    private double lat;
    private double lon;
    private boolean status;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;

    public Event() {
    }

    public Event(String title, String eventOrganiser,
                 String eventDesc, String eventUrl, String eventImageUrl, double lat, double lon,
                 HashMap<String, Object> timestampCreated) {
        this.title = title;
        this.startDateInMills = 0;
        this.endDateInMills = 0;
        this.eventOrganiser = eventOrganiser;
        this.eventDesc = eventDesc;
        this.eventImageUrl = eventImageUrl;
        this.eventUrl = eventUrl;
        this.lat = lat;
        this.lon = lon;
        this.status = true;
        this.timestampCreated = timestampCreated;
        this.timestampLastChanged = null;
    }

    public String getTitle() {
        return title;
    }

    public long getStartDateInMills() {
        return startDateInMills;
    }

    public long getEndDateInMills() {
        return endDateInMills;
    }

    public String getEventOrganiser() {
        return eventOrganiser;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public boolean isStatus() {
        return status;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public String getEventUrl() {
        return eventUrl;
    }
}
