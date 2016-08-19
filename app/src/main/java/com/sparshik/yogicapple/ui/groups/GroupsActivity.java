package com.sparshik.yogicapple.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.SupportGroup;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.utils.Constants;

public class GroupsActivity extends BaseActivity {
    private static final String LOG_TAG = GroupsActivity.class.getSimpleName();
    private GroupAdapter mGroupAdapter;
    private RecyclerView mRecyclerViewUserGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerViewUserGroups = (RecyclerView) findViewById(R.id.recyler_view_user_groups);
        mRecyclerViewUserGroups.setHasFixedSize(true);
        mRecyclerViewUserGroups.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GroupsActivity.this, JoinGroupActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseReference userGroupsRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_USER_SUPPORT_GROUPS).child(mEncodedEmail);
        mGroupAdapter = new GroupAdapter(GroupsActivity.this, SupportGroup.class, R.layout.single_group_item, GroupAdapter.GroupViewHolder.class, userGroupsRef, mEncodedEmail);
        mRecyclerViewUserGroups.setAdapter(mGroupAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGroupAdapter != null) {
            mGroupAdapter.cleanup();
        }
    }

}
