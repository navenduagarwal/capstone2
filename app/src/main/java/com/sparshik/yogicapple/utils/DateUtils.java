package com.sparshik.yogicapple.utils;

import android.content.Context;
import android.text.format.Time;

import com.sparshik.yogicapple.R;

import java.text.SimpleDateFormat;

/**
 * Utility to format dates and timestamps
 */
public class DateUtils {

    public static String getChatTimeStamp(Context context, long dateInMillis) {

        Time time = new Time();
        time.setToNow();

        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);
        // If the date we're building the String for is today's date, the format
        // is "Today at TimeStamp"

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String formattedTime = timeFormat.format(dateInMillis);
        int formatId = R.string.format_full_friendly_timestamp;

        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            return context.getString(formatId, today, formattedTime);
        } else if (julianDay == currentJulianDay - 1) {
            String yesterday = context.getString(R.string.yesterday);
            return context.getString(formatId, yesterday, formattedTime);
        } else if (julianDay > currentJulianDay - 7) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            String formattedDay = "Last " + dayFormat.format(dateInMillis);
            return context.getString(formatId, formattedDay, formattedTime);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
            String formattedDate = dateFormat.format(dateInMillis);
            return context.getString(formatId, formattedDate, formattedTime);
        }
    }
}
