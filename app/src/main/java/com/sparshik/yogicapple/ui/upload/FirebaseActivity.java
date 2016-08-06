package com.sparshik.yogicapple.ui.upload;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sparshik.yogicapple.MyDownloadService;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Upload Documents
 */
public class FirebaseActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String LOG_TAG = FirebaseActivity.class.getSimpleName();
    private int REQUEST_IMAGE_CAPTURE = 1;
    private int REQUEST_AUDIO_PATH = 2;
    private byte[] image;
    private Uri downloadUrl;
    private String selectedAudioUrl;
    private Bitmap imageBitmap;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";
    private BroadcastReceiver mDownloadReceiver;
    private ProgressDialog mProgressDialog;

    private StorageReference mStorageRef;

    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;
    private String segment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();

        final StorageReference imageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_STORAGE).child("trial");


        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }


        mDownloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView resultUrlTextView = (TextView) findViewById(R.id.download_textview);
                Log.d(LOG_TAG, "downloadReceiver:onReceive:" + intent);
                if (MyDownloadService.ACTION_COMPLETED.equals(intent.getAction()))

                {
                    String path = intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH);
                    long numBytes = intent.getLongExtra(MyDownloadService.EXTRA_BYTES_DOWNLOADED, 0);
                     // Alert success
                    resultUrlTextView.setText(String.format(Locale.getDefault(),
                            "%d bytes downloaded from %s", numBytes, path));
                }

                if (MyDownloadService.ACTION_ERROR.equals(intent.getAction()))

                {
                    String path = intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH);

                    // Alert failure
                    resultUrlTextView.setText(String.format(Locale.getDefault(),
                            "Failed to download from %s", path));
                }

                if (MyDownloadService.ACTION_PROGRESS.equals(intent.getAction())){
                    String progressDownload = intent.getStringExtra(MyDownloadService.EXTRA_PROGRESS_COMPLETED);
                    resultUrlTextView.setText(String.format(Locale.getDefault(),"Uploaded %s",progressDownload));
                    Log.d(LOG_TAG,"Reached Progress");
                }
            }
        };


        Button addImage = (Button) findViewById(R.id.button_capture_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        Button upload = (Button) findViewById(R.id.button_image_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image == null) {
                    Toast.makeText(FirebaseActivity.this, "No Image added", Toast.LENGTH_SHORT).show();
                    return;
                }
                UploadTask uploadTask = imageRef.putBytes(image);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.i(LOG_TAG, downloadUrl.toString());
                        imageBitmap = null;
                        image = null;
                        Toast.makeText(FirebaseActivity.this, "File uploaded to server", Toast.LENGTH_SHORT).show();
                        ImageView testImage = (ImageView) findViewById(R.id.captured_image);
                        testImage.setImageBitmap(imageBitmap);
                    }
                });
            }
        });


        Button selectAudio = (Button) findViewById(R.id.button_file_select);
        selectAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_AUDIO_PATH);
            }
        });

        Button uploadAudio = (Button) findViewById(R.id.button_file_upload);
        uploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedAudioUrl != null) {
                    String fileType = "audio";
                    segment = Utils.uploadFileToFirebase(mEncodedEmail, selectedAudioUrl, fileType, FirebaseActivity.this);
                } else {
                    Toast.makeText(FirebaseActivity.this, "First select file to upload", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button downloadAudio = (Button) findViewById(R.id.button_file_download);
        downloadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "hello");
                downloadAudio();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Register download receiver
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mDownloadReceiver, MyDownloadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownloadReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable(KEY_FILE_URI, mFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    /*
This method show the uploaded image in the activity layout
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ImageView testImage = (ImageView) findViewById(R.id.captured_image);
            testImage.setImageBitmap(imageBitmap);

            // Convert Bitmap to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            image = stream.toByteArray();
        } else if (requestCode == REQUEST_AUDIO_PATH && resultCode == RESULT_OK && data != null) {
            Uri selectedAudio = data.getData();

            String[] filePathColumn = {MediaStore.Audio.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedAudio,
                    filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return;
            }
            //GET PATHS
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            selectedAudioUrl = cursor.getString(columnIndex);
            Log.d(LOG_TAG, "Audiofile PATH IN PHONE: " + selectedAudioUrl);
            cursor.close();
            TextView audioUrlTextView = (TextView) findViewById(R.id.selected_url_textview);
            audioUrlTextView.setText(selectedAudioUrl);
        }
    }


    public void downloadAudio() {
        String path = "audio" + "/"
                + mEncodedEmail + "/" + segment;
        // Kick off download service
        Intent intent = new Intent(this, MyDownloadService.class);
        intent.setAction(MyDownloadService.ACTION_DOWNLOAD);
        intent.putExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH, path);
        startService(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

}