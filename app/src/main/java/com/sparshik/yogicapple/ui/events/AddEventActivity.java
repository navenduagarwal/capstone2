package com.sparshik.yogicapple.ui.events;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.Event;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.utils.ValidationUtils;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEventActivity extends BaseActivity {
    private static final String LOG_TAG = AddEventActivity.class.getSimpleName();
    private static int REQUEST_BANNER_IMAGE_FILE = 1;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    private Button mButtonSelectFile, mButtonAdd;
    private EditText mEditTextEventTitle, mEditTextEventDesc, mEditTextEventOrganizer, mEditTextEventUrl;
    private String mEventTitle, mEventOrganizerName, mEventDesc, mEventUrl, mBannerImageFilePath, mNewEventId, mUploadedBannerUrl;
    private TextView mTextViewBannerFile;
    private DatabaseReference mEventDataRef, mGeoFireRef;
    private StorageReference mEventStorageRef, bannerStorageRef;
    private ProgressDialog mAddProgressDialog;
    private double mLat, mLon;
    private GeoFire mGeoFire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        Intent intent = getIntent();
        mLat = intent.getDoubleExtra(Constants.KEY_LATITUDE, 999);
        mLon = intent.getDoubleExtra(Constants.KEY_LONGITUDE, 999);
        if (mLat == 999 || mLon == 999) {
            /* No point in continuing without a valid ID. */
            Log.e(LOG_TAG, "lat or lon intent invalid");
            startActivity(new Intent(this, EventsActivity.class));
        }

        initializeView();

        mButtonSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentImage, REQUEST_BANNER_IMAGE_FILE);
            }
        });

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEventTitle = mEditTextEventTitle.getText().toString();
                mEventDesc = mEditTextEventDesc.getText().toString();
                mEventOrganizerName = mEditTextEventOrganizer.getText().toString();
                mEventUrl = mEditTextEventUrl.getText().toString();

                boolean validTitle = isTitleValid(mEventTitle);
                boolean validDesc = isDescValid(mEventDesc);
                boolean validFile = isFileValid(mBannerImageFilePath);
                boolean validOrganizer = isOrganizerValid(mEventOrganizerName);
                boolean validUrl = ValidationUtils.isUrlValid(getApplicationContext(), mEditTextEventUrl, mEventUrl);

                if (!validTitle || !validDesc || !validFile || !validOrganizer || !validUrl) {
                    Log.d(LOG_TAG, "Donno"); //TODO
                    return;
                }
                Log.d(LOG_TAG, "yeh!");
                addBannerImage();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BANNER_IMAGE_FILE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageFile = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageFile,
                    filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return;
            }
            //GET PATHS
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mBannerImageFilePath = cursor.getString(columnIndex);
            Log.d(getClass().getSimpleName(), "Banner Image File PATH IN PHONE: " + mBannerImageFilePath);
            cursor.close();
            Uri file = Uri.fromFile(new File(mBannerImageFilePath));
            String fileName = file.getLastPathSegment();
            mTextViewBannerFile.setText(fileName);
        }

    }

    private void addBannerImage() {
        //Uploading new Icon for Program
        if (!TextUtils.isEmpty(mBannerImageFilePath)) {
            Uri fileUri = Uri.fromFile(new File(mBannerImageFilePath));
            final String localFileName = fileUri.getLastPathSegment();
            final String extension = localFileName.substring(localFileName.lastIndexOf("."));
            final String uploadFileName = mNewEventId + Constants.SUFFIX_BANNER_IMAGE + extension;
            bannerStorageRef = mEventStorageRef.child(uploadFileName);
            UploadTask uploadTask = bannerStorageRef.putFile(fileUri);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri uploadedUri = taskSnapshot.getDownloadUrl();
                            String fileName = taskSnapshot.getMetadata().getName();
                            if (uploadedUri != null && fileName != null) {
                                mUploadedBannerUrl = uploadedUri.toString();
                                Log.i(LOG_TAG, mUploadedBannerUrl);
                                mTextViewBannerFile.setText(fileName);
                            }
                            addEvent();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    mTextViewBannerFile.setText(getResources().getString(R.string.format_upload_progress, progress));
                    int progressInt = (int) progress;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                    addEvent();
                }
            });
        } else {
            addEvent();
        }
    }

    private void addEvent() {
        //Adding data to Firebase
        mAddProgressDialog.show();

        /**
         * Set raw version of date to the ServerValue.TIMESTAMP value and save into
         * timestampCreatedMap
         */
        HashMap<String, Object> timestampCreated = new HashMap<>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        timestampCreated.put(Constants.FIREBASE_PROPERTY_CREATED_BY, mEncodedEmail);
        Event newEvent = new Event(mEventTitle, mEventOrganizerName, mEventDesc, mEventUrl, mUploadedBannerUrl, mLat, mLon, timestampCreated);

        mGeoFire.setLocation(mNewEventId, new GeoLocation(mLat, mLon), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    mAddProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.error_firebase_add), Toast.LENGTH_SHORT).show();
                    deleteStorageRecords();
                }
            }
        });

        mEventDataRef.setValue(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mAddProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.record_add_success), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mAddProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.error_firebase_add), Toast.LENGTH_SHORT).show();
                deleteStorageRecords();
            }
        });
        startActivity(new Intent(this, EventsActivity.class));
    }

    private void deleteStorageRecords() {

        mGeoFire.removeLocation(mNewEventId);

        bannerStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(LOG_TAG, getResources().getString(R.string.record_delete_success, Constants.SUFFIX_ICON_IMAGE));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, getResources().getString(R.string.record_delete_error, Constants.SUFFIX_ICON_IMAGE));
            }
        });
    }


    private boolean isTitleValid(String title) {
        Pattern pattern;
        Matcher matcher;
        final String TITLE_PATTERN = "^[a-zA-Z .]{3,15}$";
        pattern = Pattern.compile(TITLE_PATTERN);
        matcher = pattern.matcher(title);
        if (!matcher.matches()) {
            mEditTextEventTitle.setError(getString(R.string.error_invalid_title));
            return false;
        }
        return true;
    }

    private boolean isOrganizerValid(String name) {
        Pattern pattern;
        Matcher matcher;
        final String NAME_PATTERN = "^[a-zA-Z .]{3,15}$";
        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            mEditTextEventOrganizer.setError(getString(R.string.error_invalid_organizer));
            return false;
        }
        return true;
    }


    private boolean isDescValid(String Desc) {
        Pattern pattern;
        Matcher matcher;
        final String DESC_PATTERN = "^[a-zA-Z .]{10,100}$";
        pattern = Pattern.compile(DESC_PATTERN);
        matcher = pattern.matcher(Desc);
        if (!matcher.matches()) {
            mEditTextEventDesc.setError(getString(R.string.error_invalid_desc));
            return false;
        }
        return true;
    }

    private boolean isFileValid(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            mTextViewBannerFile.setError(getString(R.string.error_invalid_file_name));
            return false;
        }
        return true;
    }

    private void initializeView() {
        mEditTextEventTitle = (EditText) findViewById(R.id.edit_text_event_title);
        mEditTextEventDesc = (EditText) findViewById(R.id.edit_text_event_desc);
        mEditTextEventOrganizer = (EditText) findViewById(R.id.edit_text_event_organizer);
        mEditTextEventUrl = (EditText) findViewById(R.id.edit_text_event_url);
        mButtonSelectFile = (Button) findViewById(R.id.button_select_image_file);
        mButtonAdd = (Button) findViewById(R.id.button_add);
        mTextViewBannerFile = (TextView) findViewById(R.id.text_view_selected_image_file);


        mEventDataRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_EVENTS).push();
        mNewEventId = mEventDataRef.getKey();

        mEventStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL_EVENTS);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAddProgressDialog = new ProgressDialog(this);
        mAddProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAddProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_add));
        mAddProgressDialog.setCancelable(false);

        DatabaseReference geoRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_GEOFIRE);
        mGeoFire = new GeoFire(geoRef);
    }
}


