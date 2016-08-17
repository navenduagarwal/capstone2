package com.sparshik.yogicapple.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.UserOfflineDownloads;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.utils.FileUtils;

import java.io.File;
import java.util.HashMap;

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
    public static final String EXTRA_APPLE_ID = "extra_apple_id";
    public static final String EXTRA_FILE_SUFFIX = "extra_file_suffix";
    public static final String EXTRA_DOWNLOAD_PATH = "extra_download_path";
    public static final String EXTRA_BYTES_DOWNLOADED = "extra_bytes_downloaded";
    public static final String EXTRA_PROGRESS_COMPLETED = "extra_progress_completed";
    public static final String EXTRA_DOWNLOADED_PATH = "extra_downloaded_path";
    public static final String EXTRA_ENCODED_EMAIL = "extra_encoded_email";
    private static final String TAG = "Storage#DownloadService";
    private static String mDownloadPath, mFileSuffix, mEncodedEmail, mFileName, mAppleId;
    private static HashMap<String, Integer> downloadProgresses;

    static {
        downloadProgresses = new HashMap();
    }

    private long mFileSize;
    private FirebaseStorage mStorage;
    private StorageReference fileStorageRef;
    private int mNumTasks = 0;
    private File path, localFilePath;

    public DownloadService() {
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_COMPLETED);
        filter.addAction(ACTION_ERROR);
        filter.addAction(ACTION_PROGRESS);
        return filter;
    }

    public static int getDownloadProgress(String appleId) {
        if (downloadProgresses != null) {
            Integer progress = (Integer) downloadProgresses.get(appleId);
            if (progress == null) {
                return 0;
            }
            return progress.intValue();
        }
        return 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Storage
        mStorage = FirebaseStorage.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);

        if (ACTION_DOWNLOAD.equals(intent.getAction())) {
            // Get the path to download from the intent
            mDownloadPath = intent.getStringExtra(EXTRA_DOWNLOAD_PATH);
            mFileSuffix = intent.getStringExtra(EXTRA_FILE_SUFFIX);
            mEncodedEmail = intent.getStringExtra(EXTRA_ENCODED_EMAIL);
            mAppleId = intent.getStringExtra(EXTRA_APPLE_ID);

            path = new File(FileUtils.getSaveFolder(getApplicationContext()), mFileSuffix);
            path.mkdirs();
            // Mark task started
            Log.d(TAG, ACTION_DOWNLOAD + ":" + mDownloadPath);
            taskStarted();

            fileStorageRef = mStorage.getReferenceFromUrl(mDownloadPath);

            // Download and get total bytes
            fileStorageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    // Metadata now contains the metadata for 'images/forest.jpg'
                    mFileSize = storageMetadata.getSizeBytes();
                    if (!FileUtils.diskSpaceAvailable(getApplicationContext(), mFileSize)) {
                        Log.e(TAG, "Disk Space not available");
                        return;
                    }
                    mFileName = storageMetadata.getName();
                    beginDownloadFile();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
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

    public void beginDownloadFile() {
        localFilePath = new File(path, mFileName);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String installId = preferences.getString(Constants.KEY_INSTALL_ID, null);
        if (installId == null) {
            Log.e(TAG, getString(R.string.log_error_install_id));
            return;
        }
        fileStorageRef.getFile(localFilePath)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "download:SUCCESS");

                        // Send success broadcast with number of bytes downloaded
                        Intent broadcast = new Intent(ACTION_COMPLETED);
                        broadcast.putExtra(EXTRA_DOWNLOAD_PATH, mDownloadPath);
                        broadcast.putExtra(EXTRA_BYTES_DOWNLOADED, taskSnapshot.getTotalByteCount());
                        broadcast.putExtra(EXTRA_DOWNLOADED_PATH, localFilePath);
                        LocalBroadcastManager.getInstance(getApplicationContext())
                                .sendBroadcast(broadcast);
                        Log.d(TAG, "download:SUCCESS" + taskSnapshot.getTotalByteCount());
                        Log.d(TAG, "Local file path" + localFilePath);


                        DatabaseReference userAppleStatusRef = FirebaseDatabase.getInstance()
                                .getReferenceFromUrl(Constants.FIREBASE_URL_USER_OFFLINE_DOWNLOADS)
                                .child(mEncodedEmail).child(installId).child(mAppleId);
                        UserOfflineDownloads userAppleStatus = new UserOfflineDownloads(localFilePath.toString());
                        userAppleStatusRef.setValue(userAppleStatus);
                        // Mark task completed
                        taskCompleted();
                    }
                })
                .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //Send progress broadcast
                        double progress = 100 * (taskSnapshot.getBytesTransferred()) / mFileSize;
                        Intent broadcast = new Intent(ACTION_PROGRESS);
                        broadcast.putExtra(EXTRA_PROGRESS_COMPLETED, progress);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
                        downloadProgresses.put(mAppleId, (int) progress);
                        taskCompleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.w(TAG, "download:FAILURE", exception);

                        // Send failure broadcast
                        Intent broadcast = new Intent(ACTION_ERROR);
                        broadcast.putExtra(EXTRA_DOWNLOAD_PATH, mDownloadPath);
                        LocalBroadcastManager.getInstance(getApplicationContext())
                                .sendBroadcast(broadcast);

                        // Mark task completed
                        taskCompleted();
                    }
                });
    }
}
