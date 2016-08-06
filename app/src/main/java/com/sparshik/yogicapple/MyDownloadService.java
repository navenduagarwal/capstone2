package com.sparshik.yogicapple;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.sparshik.yogicapple.utils.Constants;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * To download file from Firebase
 */
public class MyDownloadService extends Service {
    private static final String TAG = "Storage#DownloadService";

    /** Actions **/
    public static final String ACTION_DOWNLOAD = "action_download";
    public static final String ACTION_COMPLETED = "action_completed";
    public static final String ACTION_ERROR = "action_error";
    public static final String ACTION_PROGRESS ="action_progress";

    /** Extras **/
    public static final String EXTRA_DOWNLOAD_PATH = "extra_download_path";
    public static final String EXTRA_BYTES_DOWNLOADED = "extra_bytes_downloaded";
    public static final String EXTRA_PROGRESS_COMPLETED = "extra_progress_completed";

    private StorageReference mStorage;
    private int mNumTasks = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Storage
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_STORAGE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);

        if (ACTION_DOWNLOAD.equals(intent.getAction())) {
            // Get the path to download from the intent
            final String downloadPath = intent.getStringExtra(EXTRA_DOWNLOAD_PATH);

            // Mark task started
            Log.d(TAG, ACTION_DOWNLOAD + ":" + downloadPath);
            taskStarted();

            // Download and get total bytes
            mStorage.child(downloadPath).getStream()
                    .addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "download:SUCCESS");

                            // Send success broadcast with number of bytes downloaded
                            Intent broadcast = new Intent(ACTION_COMPLETED);
                            broadcast.putExtra(EXTRA_DOWNLOAD_PATH, downloadPath);
                            broadcast.putExtra(EXTRA_BYTES_DOWNLOADED, taskSnapshot.getTotalByteCount());
                            LocalBroadcastManager.getInstance(getApplicationContext())
                                    .sendBroadcast(broadcast);
                            Log.d(TAG, "download:SUCCESS" + taskSnapshot.getTotalByteCount());
                            // Mark task completed
                            taskCompleted();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<StreamDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                            //Send progress broadcast
                            double progress = taskSnapshot.getBytesTransferred();
                  Log.d(TAG,"Progress"+progress);
                            Intent broadcast = new Intent(ACTION_PROGRESS);
                            broadcast.putExtra(EXTRA_PROGRESS_COMPLETED,progress);
                            LocalBroadcastManager.getInstance(getApplicationContext())
                            .sendBroadcast(broadcast);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.w(TAG, "download:FAILURE", exception);

                            // Send failure broadcast
                            Intent broadcast = new Intent(ACTION_ERROR);
                            broadcast.putExtra(EXTRA_DOWNLOAD_PATH, downloadPath);
                            LocalBroadcastManager.getInstance(getApplicationContext())
                                    .sendBroadcast(broadcast);

                            // Mark task completed
                            taskCompleted();
                        }
                    });

        }

        return START_REDELIVER_INTENT;
    }

    private void taskStarted() {
        changeNumberOfTasks(1);
    }

    private void taskCompleted() {
        changeNumberOfTasks(-1);
    }

    private synchronized void changeNumberOfTasks(int delta) {
        Log.d(TAG, "changeNumberOfTasks:" + mNumTasks + ":" + delta);
        mNumTasks += delta;

        // If there are no tasks left, stop the service
        if (mNumTasks <= 0) {
            Log.d(TAG, "stopping");
            stopSelf();
        }
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_COMPLETED);
        filter.addAction(ACTION_ERROR);

        return filter;
    }
}
