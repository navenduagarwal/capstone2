package com.sparshik.yogicapple.utils;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.AudioFile;
import com.sparshik.yogicapple.model.User;
import com.sparshik.yogicapple.views.CircleProgressBar;

import java.io.File;
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

    public static void createUserInFirebaseHelper(final String mUserEmail, final String mUserName, final String uid) {
        final String encodedEmail = FireBaseUtils.encodeEmail(mUserEmail);
        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL);
        /**
         * See if there is already a user (for example, if they already logged in with an associated
         * Google account.
         */
        HashMap<String, Object> userAndUidMapping = new HashMap<String, Object>();
                     /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
        HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        User newUser = new User(encodedEmail, mUserName, timestampJoined);
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

    public static String uploadFileToFirebase(String encodedEmail, String filePath, String fileType, Activity activity) {
        if (filePath != null && fileType != null) {
            Uri file = Uri.fromFile(new File(filePath));
            final String name = file.getLastPathSegment();
            String extension = file.getLastPathSegment();
            extension = extension.substring(extension.lastIndexOf("."));
            final DatabaseReference firebaseRef = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl(Constants.FIREBASE_URL_AUDIOS).child("Introduction");
            final DatabaseReference audioFileRef = firebaseRef.push();
//            final String filename = audioFileRef.getKey()+extension;

            final StorageReference fileRef = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(Constants.FIREBASE_URL_STORAGE).child(fileType + "/"
                            + encodedEmail + "/" + name);
            UploadTask uploadTask = fileRef.putFile(file);
            final TextView audioUrlTextView = (TextView) activity.findViewById(R.id.selected_url_textview);
            final TextView resultUrlTextView = (TextView) activity.findViewById(R.id.link_firebase_uploaded_textview);
            final CircleProgressBar progressView = (CircleProgressBar) activity.findViewById(R.id.progress);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null) {
                        Log.i(LOG_TAG, downloadUrl.toString());
                        HashMap<String, Object> timestampCreated = new HashMap<>();
                        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
                        AudioFile audioFile = new AudioFile(1, false, downloadUrl.toString(), timestampCreated);
                        audioFileRef.setValue(audioFile);
                        audioUrlTextView.setText(name);
                        resultUrlTextView.setText(downloadUrl.toString());
                    }
                }
            })// Observe state change events such as progress, pause, and resume
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            audioUrlTextView.setText("Upload is " + progress + "% done");
                            progressView.setProgress((int) progress);
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    audioUrlTextView.setText("Upload is paused");
                }
            });
            return name;
        }
        return null;
    }
}
