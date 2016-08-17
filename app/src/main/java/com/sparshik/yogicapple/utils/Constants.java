package com.sparshik.yogicapple.utils;

import com.sparshik.yogicapple.BuildConfig;

/**
 * Constants class store most important strings and paths of the app
 */
public class Constants {

    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_UID_MAPPINGS = "uidMappings";
    public static final String FIREBASE_LOCATION_PROGRAMS = "programs";
    public static final String FIREBASE_LOCATION_PROGRAM_PACKS = "programPacks";
    public static final String FIREBASE_LOCATION_PACK_APPLES = "packApples";
    public static final String FIREBASE_LOCATION_USER_OFFLINE_DOWNLOADS = "userOfflineDownloads";

    /**
     * Constants for Firebase object properties
     */

    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    public static final String FIREBASE_PROPERTY_CREATED_BY = "createdBy";
    public static final String FIREBASE_PROPERTY_LAST_CHANGED_BY = "lastChangedBy";
    public static final String FIREBASE_PROPERTY_USER_IS_VERIFIED = "verified";
    public static final String FIREBASE_PROPERTY_APPLE_SEQ_NUMBER = "appleSeqNumber";

    /**
     * Constants for Firebase Database URL
     */
    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_PROGRAMS = FIREBASE_URL + "/" + FIREBASE_LOCATION_PROGRAMS;
    public static final String FIREBASE_URL_PROGRAM_PACKS = FIREBASE_URL + "/" + FIREBASE_LOCATION_PROGRAM_PACKS;
    public static final String FIREBASE_URL_PACK_APPLES = FIREBASE_URL + "/" + FIREBASE_LOCATION_PACK_APPLES;
    public static final String FIREBASE_URL_USER_OFFLINE_DOWNLOADS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_OFFLINE_DOWNLOADS;

    /**
     * Constants for Firebase Storage URL
     */
    public static final String FIREBASE_STORAGE_URL = BuildConfig.FIREBASE_STORAGE_ROOT_URL;
    public static final String FIREBASE_STORAGE_URL_PROGRAMS = FIREBASE_STORAGE_URL + "/" + FIREBASE_LOCATION_PROGRAMS;
    public static final String FIREBASE_STORAGE_URL_PROGRAM_PACKS = FIREBASE_STORAGE_URL + "/" + FIREBASE_LOCATION_PROGRAM_PACKS;
    public static final String FIREBASE_STORAGE_URL_PACK_APPLES = FIREBASE_STORAGE_URL + "/" + FIREBASE_LOCATION_PACK_APPLES;

    /**
     * Constants for bundles, extras and shared preferences keys
     */
    public static final String KEY_LAYOUT_RESOURCE = "LAYOUT_RESOURCE";
    public static final String KEY_ENCODED_EMAIL = "ENCODED_EMAIL";
    public static final String KEY_PROVIDER_ID = "PROVIDER_ID";
    public static final String KEY_SIGNUP_EMAIL = "SIGNUP_EMAIL";
    public static final String KEY_PROGRAM_ID = "PROGRAM_ID";
    public static final String KEY_PACK_ID = "PACK_ID";
    public static final String KEY_APPLE_ID = "APPLE_ID";
    public static final String KEY_AUDIO_URL = "AUDIO_URL";
    public static final String KEY_INSTALL_ID = "INSTALL_ID";
    public static final String KEY_CURRENT_PROGRAM_ID = "CURRENT_PROGRAM_ID";
    public static final String KEY_CURRENT_PACK_ID = "CURRENT_PACK_ID";

    /**
     * Constants for Firebase login
     */
    public static final String PASSWORD_PROVIDER = "password";
    public static final String GOOGLE_PROVIDER = "google.com";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";


    /**
     * Suffix for firebase storage files
     */
    public static final String SUFFIX_AUDIO = "_audio";
    public static final String SUFFIX_VIDEO = "_video";
    public static final String SUFFIX_ICON_IMAGE = "_iconImage";
    public static final String SUFFIX_BANNER_IMAGE = "_bannerImage";

    public static final String DEFUALT_PROGRAM_COLOR = "#616161";

}
