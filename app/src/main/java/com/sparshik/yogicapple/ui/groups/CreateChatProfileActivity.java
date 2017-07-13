package com.sparshik.yogicapple.ui.groups;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.UserChatProfile;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.utils.Constants;

import java.io.File;

public class CreateChatProfileActivity extends BaseActivity {
    private static final String LOG_TAG = CreateChatProfileActivity.class.getSimpleName();
    private static int REQUEST_PROFILE_IMAGE_FILE = 1;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    private EditText mEditTextNickName;
    private TextView mTextViewProfilePic;
    private Button mButtonSelectFile, mButtonCreate;
    private String mNickname, mProfilePicImageFilePath, mUploadedPicUrl;
    private DatabaseReference mChatProfileRef;
    private ProgressDialog mAddProgressDialog;
    private StorageReference mGroupStorageRef, profileImageStorageRef;
    private UserChatProfile mUserChatProfile;
//    private String groupId, groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_profile);

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

        initializeScreen();

        mButtonSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentImage, REQUEST_PROFILE_IMAGE_FILE);
            }
        });


        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNickname = mEditTextNickName.getText().toString();
                if (mProfilePicImageFilePath != null && !TextUtils.isEmpty(mNickname)) {
                    addProfileImage();
                }
            }
        });

    }

    public void initializeScreen() {
        mTextViewProfilePic = (TextView) findViewById(R.id.text_view_selected_profile_pic);
        mButtonSelectFile = (Button) findViewById(R.id.button_select_profile_pic);
        mButtonCreate = (Button) findViewById(R.id.button_create);
        mEditTextNickName = (EditText) findViewById(R.id.edit_text_nick_name);
        mChatProfileRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_GROUP_CHAT_PROFILES).child(mEncodedEmail);

        mGroupStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL_SUPPORT_GROUPS);

               /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAddProgressDialog = new ProgressDialog(this);
        mAddProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAddProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_add));
        mAddProgressDialog.setCancelable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PROFILE_IMAGE_FILE && resultCode == Activity.RESULT_OK && data != null) {
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
            mProfilePicImageFilePath = cursor.getString(columnIndex);
            Log.d(getClass().getSimpleName(), "Profile Image File PATH IN PHONE: " + mProfilePicImageFilePath);
            cursor.close();
            Uri file = Uri.fromFile(new File(mProfilePicImageFilePath));
            String fileName = file.getLastPathSegment();
            mTextViewProfilePic.setText(fileName);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void addProfileImage() {

        //Uploading new Icon for Program
        if (!TextUtils.isEmpty(mProfilePicImageFilePath)) {
            Uri fileUri = Uri.fromFile(new File(mProfilePicImageFilePath));
            final String localFileName = fileUri.getLastPathSegment();
            final String extension = localFileName.substring(localFileName.lastIndexOf("."));
            final String uploadFileName = mEncodedEmail + Constants.SUFFIX_ICON_IMAGE + extension;
            profileImageStorageRef = mGroupStorageRef.child(uploadFileName);
            UploadTask uploadTask = profileImageStorageRef.putFile(fileUri);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") Uri uploadedUri = taskSnapshot.getDownloadUrl();
                            @SuppressWarnings("VisibleForTests") String fileName = taskSnapshot.getMetadata().getName();
                            if (uploadedUri != null && fileName != null) {
                                mUploadedPicUrl = uploadedUri.toString();
                                Log.i(LOG_TAG, mUploadedPicUrl);
                                mTextViewProfilePic.setText(fileName);
                            }
                            addChatProfile();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") double progress = ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    mTextViewProfilePic.setText(getResources().getString(R.string.format_upload_progress, progress));
                    int progressInt = (int) progress;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                    addChatProfile();
                }
            });
        } else {
            addChatProfile();
        }


    }

    public void addChatProfile() {
        mUserChatProfile = new UserChatProfile(mNickname, mUploadedPicUrl);
        mAddProgressDialog.show();
        mChatProfileRef.setValue(mUserChatProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mAddProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.record_add_success), Toast.LENGTH_SHORT).show();

                SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(CreateChatProfileActivity.this);
                SharedPreferences.Editor spe = mSharedPreferences.edit();
                spe.putString(Constants.KEY_CHAT_NICK_NAME, mNickname).apply();
                spe.putString(Constants.KEY_CHAT_PROFILE_IMAGE_URL, mUploadedPicUrl).apply();

                Intent intentChat = new Intent(CreateChatProfileActivity.this, GroupsActivity.class);
//                intentChat.putExtra(Constants.KEY_GROUP_ID, groupId);
//                intentChat.putExtra(Constants.KEY_GROUP_NAME, groupName);
                startActivity(intentChat);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mAddProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.error_firebase_add), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
