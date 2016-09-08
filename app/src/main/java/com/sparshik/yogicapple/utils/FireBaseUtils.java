package com.sparshik.yogicapple.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.sparshik.yogicapple.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility Class for Firebase
 */
public class FireBaseUtils {


    private static final String LOG_TAG = FireBaseUtils.class.getSimpleName();

    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    public static void createUserInFirebaseHelper(Context context, final String mUserEmail, final String mUserName, final String uid, final String provider) {
        final String encodedEmail = FireBaseUtils.encodeEmail(mUserEmail);
        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL);
        boolean verified = false;
        if (provider.equals(Constants.GOOGLE_PROVIDER)) {
            verified = true;
        }
        /**
         * See if there is already a user (for example, if they already logged in with an associated
         * Google account.
         */
        HashMap<String, Object> userAndUidMapping = new HashMap<>();
                     /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
        HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        String programId = "-KPE5BVPM7DiGyHN5yZ5";
        String packId = "-KPEAjnMfnFsYRuw8cee";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = preferences.edit();
        spe.putString(Constants.KEY_CURRENT_PROGRAM_ID, programId).apply();
        spe.putString(Constants.KEY_CURRENT_PACK_ID, packId).apply();

        User newUser = new User(encodedEmail, mUserName, verified, programId, packId, timestampJoined);
        HashMap<String, Object> newUserMap = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newUser, Map.class);
                     /* Add the user and UID to the update map */
        userAndUidMapping.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + encodedEmail,
                newUserMap);
        userAndUidMapping.put("/" + Constants.FIREBASE_LOCATION_UID_MAPPINGS + "/"
                + uid, encodedEmail);
        /* Try to update the database; if there is already a user, this will fail */
        firebaseRef.updateChildren(userAndUidMapping).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                             /* Try just making a uid mapping */
                    firebaseRef.child(Constants.FIREBASE_LOCATION_UID_MAPPINGS)
                            .child(uid).setValue(encodedEmail);
                }
            }
        });
    }

}
