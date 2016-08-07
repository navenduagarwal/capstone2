package com.sparshik.yogicapple.utils;

import android.app.Activity;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Maintain Global Variables
 */
public class Globals {
    public static Activity iActivity;
    public static int iAppVersionCode;
    public static String iAppVersionName;
    public static String iContentLocale;
    public static Resources iResources;
    public static int iScreenHeight;
    public static int iScreenWidth;
    public static Locale iLocale;
    public static String iUserAgent;
    public static boolean isDebuggable;
    public static boolean isGoogleDevice;
    public static boolean justLoadedJourney;
    public static boolean useHdVideos;

    static {
        iAppVersionCode = -1;
        justLoadedJourney = false;
        iContentLocale = "en_US";
        useHdVideos = false;
        isGoogleDevice = true;
        isDebuggable = false;
    }

}
