package com.sparshik.yogicapple.ui.groups;

import android.content.Intent;
import android.os.Bundle;
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

                    Log.d(LOG_TAG, mEncodedEmail + userProfileUrl + userChatName);
                    populateAdapter(userChatName, userProfileUrl);

                } else {
                    Intent intentCreate = new Intent(GroupsActivity.this, CreateChatProfileActivity.class);
                    GroupsActivity.this.startActivity(intentCreate);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
