package com.sparshik.yogicapple.ui.groups;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.ChatImageOption;
import com.sparshik.yogicapple.model.UserChatProfile;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.utils.Constants;

import java.util.ArrayList;

public class CreateChatProfileActivity extends BaseActivity {
    private static final String LOG_TAG = CreateChatProfileActivity.class.getSimpleName();
    private GridView mGridView;
    private GridViewAdapter mGridViewAdapter;
    private EditText mEditTextNickName;
    private Button mButtonCreate;
    private String mNickname;
    private DatabaseReference mChatProfileRef;
    private ProgressDialog mAddProgressDialog;
    private int mChatProfileImageResId;
    private UserChatProfile mUserChatProfile;
    private String groupId;

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

        Intent intent = getIntent();
        groupId = intent.getStringExtra(Constants.KEY_GROUP_ID);
        if (groupId == null) {
            startActivity(new Intent(CreateChatProfileActivity.this, GroupsActivity.class));
            Toast.makeText(this, "Group id not found", Toast.LENGTH_SHORT).show(); //TODO need to remove this
        }

        initializeScreen();

        mGridViewAdapter = new GridViewAdapter(CreateChatProfileActivity.this, R.layout.grid_chat_profile_item_layout, getData());
        mGridView.setAdapter(mGridViewAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatImageOption imageOption = (ChatImageOption) adapterView.getItemAtPosition(i);
                mChatProfileImageResId = imageOption.getChatImageResId();
            }
        });

        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNickname = mEditTextNickName.getText().toString();
                if (mChatProfileImageResId != 0 && !TextUtils.isEmpty(mNickname)) {
                    addChatProfile();
                }
            }
        });

    }

    public void initializeScreen() {
        mGridView = (GridView) findViewById(R.id.grid_view_chat_image_picker);
        mButtonCreate = (Button) findViewById(R.id.button_create);
        mEditTextNickName = (EditText) findViewById(R.id.edit_text_nick_name);
        mChatProfileRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_GROUP_CHAT_PROFILES).child(mEncodedEmail);
               /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAddProgressDialog = new ProgressDialog(this);
        mAddProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAddProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_add));
        mAddProgressDialog.setCancelable(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGridViewAdapter != null) {
            mGridViewAdapter.clear();
        }
    }

    private ArrayList<ChatImageOption> getData() {
        final ArrayList<ChatImageOption> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_avatar_ids);
        for (int i = 0; i < imgs.length(); i++) {
            int resId = imgs.getResourceId(i, -1);
            imageItems.add(new ChatImageOption(resId));
        }
        return imageItems;
    }

    public void addChatProfile() {
        mUserChatProfile = new UserChatProfile(mNickname, mChatProfileImageResId);
        mAddProgressDialog.show();
        mChatProfileRef.setValue(mUserChatProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mAddProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.record_add_success), Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreateChatProfileActivity.this);
                SharedPreferences.Editor spe = preferences.edit();
                spe.putString(Constants.KEY_CHAT_NICK_NAME, mNickname).apply();
                spe.putInt(Constants.KEY_CHAT_PROFILE_IMAGE_RES_ID, mChatProfileImageResId).apply();
                Intent intentChat = new Intent(CreateChatProfileActivity.this, GroupChatActivity.class);
                intentChat.putExtra(Constants.KEY_GROUP_ID, groupId);
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
