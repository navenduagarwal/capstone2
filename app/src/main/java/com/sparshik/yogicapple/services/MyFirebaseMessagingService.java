package com.sparshik.yogicapple.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * handles notifications from firebase server
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String LOG_TAG = "UserFMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Handle data payload of FCM messages.
        Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
        Log.d(LOG_TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(LOG_TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        if (remoteMessage.getData().size() > 0) {
            Log.d(LOG_TAG, "FCM Data Message: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(LOG_TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
