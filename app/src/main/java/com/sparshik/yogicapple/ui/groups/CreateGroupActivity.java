package com.sparshik.yogicapple.ui.groups;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.SupportGroup;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.utils.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateGroupActivity extends BaseActivity {
    private static final String LOG_TAG = CreateGroupActivity.class.getSimpleName();
    private static int REQUEST_ICON_IMAGE_FILE = 1;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    private EditText mEditTextGroupName, mEditTextGroupDesc;
    private TextView mTextViewIconFile;
    private Button mButtonSelectFile, mButtonAdd;
    private String mGroupName, mGroupDesc, mIconImageFilePath, mNewGroupId, mUploadedIconUrl;
    private StorageReference mGroupStorageRef, iconStorageRef;
    private DatabaseReference mGroupDataRef;
    private ProgressDialog mAddProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

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

        initializeView();

        mButtonSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentImage, REQUEST_ICON_IMAGE_FILE);
            }
        });

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupName = mEditTextGroupName.getText().toString();
                mGroupDesc = mEditTextGroupDesc.getText().toString();

//                boolean validName = isNameValid(mGroupName);
//                boolean validDesc = isDescValid(mGroupDesc);
//                boolean validFileName = isFileNameValid(mIconImageFilePath);
//
//                if (!validName || !validDesc || !validFileName) {
//                    return;
//                }

                addIconImage();
            }
        });

    }

    public boolean isNameValid(String name) {
        final String NAME_REGEX = "^[a-zA-Z]+[\\-'\\s]?[a-zA-Z ]{3,40}$";
        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(name);

        if (!matcher.matches()) {
            Log.e(LOG_TAG, matcher.toMatchResult().toString());
            mEditTextGroupName.setError(getString(R.string.error_invalid_name));
            return false;
        }
        return true;
    }

    public boolean isDescValid(String desc) {
        final String DESC_REGEX = "^[a-zA-Z]+[\\-'\\s]?[a-zA-Z ]{10,200}$";
        Pattern pattern = Pattern.compile(DESC_REGEX, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(desc);

        if (!matcher.matches()) {
            Log.e(LOG_TAG, matcher.toMatchResult().toString());
            mEditTextGroupDesc.setError(getString(R.string.error_invalid_desc));
            return false;
        }
        return true;
    }

    public boolean isFileNameValid(String mIconImageFilePath) {
        File file = new File(mIconImageFilePath);
        if (!file.exists()) {
            mTextViewIconFile.setError(getString(R.string.error_invalid_file_name));
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ICON_IMAGE_FILE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedIconFile = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedIconFile,
                    filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return;
            }
            //GET PATHS
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mIconImageFilePath = cursor.getString(columnIndex);
            Log.d(getClass().getSimpleName(), "Icon Image File PATH IN PHONE: " + mIconImageFilePath);
            cursor.close();
            Uri file = Uri.fromFile(new File(mIconImageFilePath));
            String fileName = file.getLastPathSegment();
            mTextViewIconFile.setText(fileName);
        }

    }

    public void initializeView() {

        mEditTextGroupName = (EditText) findViewById(R.id.edit_text_group_name);
        mEditTextGroupDesc = (EditText) findViewById(R.id.edit_text_group_desc);
        mTextViewIconFile = (TextView) findViewById(R.id.text_view_selected_icon_file);
        mButtonSelectFile = (Button) findViewById(R.id.button_select_icon_file);
        mButtonAdd = (Button) findViewById(R.id.button_add);

        mGroupDataRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_SUPPORT_GROUPS).push();
        mNewGroupId = mGroupDataRef.getKey();

        mGroupStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL_SUPPORT_GROUPS);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAddProgressDialog = new ProgressDialog(this);
        mAddProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAddProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_add));
        mAddProgressDialog.setCancelable(false);

    }

    public void addIconImage() {
        //Uploading new Icon for Program
        if (!TextUtils.isEmpty(mIconImageFilePath)) {
            Uri fileUri = Uri.fromFile(new File(mIconImageFilePath));
            final String localFileName = fileUri.getLastPathSegment();
            final String extension = localFileName.substring(localFileName.lastIndexOf("."));
            final String uploadFileName = mNewGroupId + Constants.SUFFIX_ICON_IMAGE + extension;
            iconStorageRef = mGroupStorageRef.child(uploadFileName);
            UploadTask uploadTask = iconStorageRef.putFile(fileUri);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") Uri uploadedUri = taskSnapshot.getDownloadUrl();
                            @SuppressWarnings("VisibleForTests") String fileName = taskSnapshot.getMetadata().getName();
                            if (uploadedUri != null && fileName != null) {
                                mUploadedIconUrl = uploadedUri.toString();
                                Log.i(LOG_TAG, mUploadedIconUrl);
                                mTextViewIconFile.setText(fileName);
                            }
                            createGroup();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") double progress = ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    mTextViewIconFile.setText(getResources().getString(R.string.format_upload_progress, progress));
                    int progressInt = (int) progress;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                    createGroup();
                }
            });
        } else {
            createGroup();
        }

    }

    public void createGroup() {
        //Adding data to Firebase
        mAddProgressDialog.show();

        /**
         * Set raw version of date to the ServerValue.TIMESTAMP value and save into
         * timestampCreatedMap
         */
        HashMap<String, Object> timestampCreated = new HashMap<>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        timestampCreated.put(Constants.FIREBASE_PROPERTY_CREATED_BY, mEncodedEmail);

        SupportGroup newGroup = new SupportGroup(mGroupName, mGroupDesc, mUploadedIconUrl, timestampCreated);

        mGroupDataRef.setValue(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        startActivity(new Intent(this, JoinGroupActivity.class));
    }

    public void deleteStorageRecords() {
        iconStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
