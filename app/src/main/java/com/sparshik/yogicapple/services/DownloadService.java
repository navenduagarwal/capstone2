package com.sparshik.yogicapple.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.utils.FileUtils;

import java.io.File;

/**
 * To download file from Firebase
 */
public class DownloadService extends Service {
    /**
     * Actions
     **/
    public static final String ACTION_DOWNLOAD = "action_download";
    public static final String ACTION_COMPLETED = "action_completed";
    public static final String ACTION_ERROR = "action_error";
    public static final String ACTION_PROGRESS = "action_progress";
    /**
     * Extras
     **/
    public static final String EXTRA_DOWNLOAD_PATH = "extra_download_path";
    public static final String EXTRA_BYTES_DOWNLOADED = "extra_bytes_downloaded";
    public static final String EXTRA_PROGRESS_COMPLETED = "extra_progress_completed";
    public static final String EXTRA_FILE_SIZE = "extra_file_size";
    public static final String EXTRA_DOWNLOADED_PATH = "extra_downloaded_path";
    private static final String TAG = "Storage#DownloadService";
    private StorageReference mStorage;
    private int mNumTasks = 0;
    private File path, localFile;

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_COMPLETED);
        filter.addAction(ACTION_ERROR);

        return filter;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Storage
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_STORAGE);
        path = new File(FileUtils.getSaveFolder(getApplicationContext()), "misc");
        path.mkdirs();
        localFile = new File(path, "ankita.mp3");
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
            final double filesize = intent.getDoubleExtra(EXTRA_FILE_SIZE, 0.00);
            Log.d(TAG, ACTION_DOWNLOAD + ":" + downloadPath);
            taskStarted();

            // Download and get total bytes
            StorageReference fileStorage = mStorage.child(downloadPath);
            fileStorage.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    // Metadata now contains the metadata for 'images/forest.jpg'
                    if (!FileUtils.diskSpaceAvailable(getApplicationContext(), storageMetadata.getSizeBytes())) {
                        Log.e(TAG, "Disk Space not available");
                        return;
                    }
                    ;

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });


            fileStorage.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "download:SUCCESS");

                            // Send success broadcast with number of bytes downloaded
                            Intent broadcast = new Intent(ACTION_COMPLETED);
                            broadcast.putExtra(EXTRA_DOWNLOAD_PATH, downloadPath);
                            broadcast.putExtra(EXTRA_BYTES_DOWNLOADED, taskSnapshot.getTotalByteCount());
                            broadcast.putExtra(EXTRA_DOWNLOADED_PATH, localFile);
                            LocalBroadcastManager.getInstance(getApplicationContext())
                                    .sendBroadcast(broadcast);
                            Log.d(TAG, "download:SUCCESS" + taskSnapshot.getTotalByteCount());
                            // Mark task completed
                            taskCompleted();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            //Send progress broadcast
                            double progress = 100 * (taskSnapshot.getBytesTransferred() / filesize);
                            Log.d(TAG, "Progress" + progress);
                            Intent broadcast = new Intent(ACTION_PROGRESS);
                            broadcast.putExtra(EXTRA_PROGRESS_COMPLETED, progress);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
                            taskCompleted();
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
}
