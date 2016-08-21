package com.sparshik.yogicapple.ui.groups;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.SupportGroup;
import com.sparshik.yogicapple.model.UserChatProfile;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.utils.Constants;

public class GroupsActivity extends BaseActivity {
    private static final String LOG_TAG = GroupsActivity.class.getSimpleName();
    private GroupAdapter mGroupAdapter;
    private RecyclerView mRecyclerViewUserGroups;
    private DatabaseReference mChatProfileRef, userGroupsRef;
    private ValueEventListener mChatProfileRefValueListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GroupsActivity.this, JoinGroupActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d(LOG_TAG, mEncodedEmail + "outside");


        mChatProfileRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_GROUP_CHAT_PROFILES).child(mEncodedEmail);
        mChatProfileRefValueListener = mChatProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserChatProfile userChatProfile = dataSnapshot.getValue(UserChatProfile.class);
                if (userChatProfile != null) {
                    String userChatName = userChatProfile.getNickName();
                    String userProfileUrl = userChatProfile.getChatProfilePicUrl();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GroupsActivity.this);
                    SharedPreferences.Editor spe = preferences.edit();
                    spe.putString(Constants.KEY_CHAT_NICK_NAME, userChatName).apply();
                    spe.putString(Constants.KEY_CHAT_PROFILE_IMAGE_URL, userProfileUrl).apply();
                    Log.d(LOG_TAG, mEncodedEmail + userProfileUrl + userChatName);
                } else {
                    Intent intentCreate = new Intent(GroupsActivity.this, CreateChatProfileActivity.class);
                    GroupsActivity.this.startActivity(intentCreate);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userChatName = sharedPreferences.getString(Constants.KEY_CHAT_NICK_NAME, "navenduDefault");
        String userProfileUrl = sharedPreferences.getString(Constants.KEY_CHAT_PROFILE_IMAGE_URL, "https://firebasestorage.googleapis.com/v0/b/yogicapple-b288e.appspot.com/o/supportGroups%2Fnavendu%2Cagarwal%40gmail%2Ccom_iconImage.jpg?alt=media&token=5cf87141-8d7a-42f7-b99b-c8c35323778f");
        populateAdapter(userChatName, userProfileUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGroupAdapter != null) {
            mGroupAdapter.cleanup();
        }
        mChatProfileRef.removeEventListener(mChatProfileRefValueListener);
    }

    public void populateAdapter(String userChatName, String userProfileUrl) {
        userGroupsRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_USER_SUPPORT_GROUPS).child(mEncodedEmail);
        mRecyclerViewUserGroups = (RecyclerView) findViewById(R.id.recycler_view_user_groups);
        mRecyclerViewUserGroups.setHasFixedSize(true);
        mRecyclerViewUserGroups.setLayoutManager(new LinearLayoutManager(GroupsActivity.this));
        mGroupAdapter = new GroupAdapter(GroupsActivity.this, SupportGroup.class, R.layout.single_group_item, GroupAdapter.GroupViewHolder.class
                , userGroupsRef, mEncodedEmail, userChatName, userProfileUrl);
        mRecyclerViewUserGroups.setAdapter(mGroupAdapter);
        Log.d(LOG_TAG, mEncodedEmail + userProfileUrl + userChatName + "inside populate");
    }
}
