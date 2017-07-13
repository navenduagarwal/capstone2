package com.sparshik.yogicapple.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.SupportGroup;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.ui.viewholders.AutoCompleteGroupViewHolder;
import com.sparshik.yogicapple.utils.Constants;

public class JoinGroupActivity extends BaseActivity {
    private EditText mEditTextJoinGroupName;
    private RecyclerView mRecyclerViewAutocomplete;
    private AutocompleteGroupAdapter mGroupsAutoCompleteAdapter;
    private DatabaseReference mGroupsRef;
    private String mInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        mGroupsRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_SUPPORT_GROUPS);

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();

        /**
         * Set interactive bits, such as click events/adapters
         */
        mEditTextJoinGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            /* Get the input after every textchanged event */
                mInput = mEditTextJoinGroupName.getText().toString();

                /* Clean up the old adapter */
                if (mGroupsAutoCompleteAdapter != null) mGroupsAutoCompleteAdapter.cleanup();
                /* Nullify the adapter data if the input length is less than 2 characters */
                if (mInput.equals("") || mInput.length() < 2) {
                    Query autoSearchQuery = mGroupsRef.orderByChild(Constants.FIREBASE_PROPERTY_GROUP_NAME).limitToFirst(10);
                    mGroupsAutoCompleteAdapter = new AutocompleteGroupAdapter(JoinGroupActivity.this, SupportGroup.class, R.layout.single_autocomplete_group_item,
                            AutoCompleteGroupViewHolder.class, autoSearchQuery, mEncodedEmail);
                    mRecyclerViewAutocomplete.setAdapter(mGroupsAutoCompleteAdapter);
                } else {
                    Query autoSearchQuery = mGroupsRef.orderByChild(Constants.FIREBASE_PROPERTY_GROUP_NAME).startAt(mInput).endAt(mInput + "~").limitToFirst(5);
                    mGroupsAutoCompleteAdapter = new AutocompleteGroupAdapter(JoinGroupActivity.this, SupportGroup.class, R.layout.single_autocomplete_group_item,
                            AutoCompleteGroupViewHolder.class, autoSearchQuery, mEncodedEmail);
                    mRecyclerViewAutocomplete.setAdapter(mGroupsAutoCompleteAdapter);
                }
            }
        });
    }


    /**
     * Override onOptionsItemSelected to use main_menu instead of BaseActivity menu
     *
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_join_group, menu);
        return true;
    }

    /**
     * Override onOptionsItemSelected to add action_settings only to the MainActivity
     *
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create_groups) {
            startActivity(new Intent(JoinGroupActivity.this, CreateGroupActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGroupsAutoCompleteAdapter != null) {
            mGroupsAutoCompleteAdapter.cleanup();
        }
    }

    /**
     * Link layout elements from XML and setup the toolbar
     */
    public void initializeScreen() {
        mEditTextJoinGroupName = (EditText) findViewById(R.id.edit_text_join_group_name);
        mRecyclerViewAutocomplete = (RecyclerView) findViewById(R.id.recyle_view_groups_autocomplete);
        mRecyclerViewAutocomplete.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewAutocomplete.setHasFixedSize(true);
        Query autoSearchQuery = mGroupsRef.orderByChild(Constants.FIREBASE_PROPERTY_GROUP_NAME).limitToFirst(10);
        mGroupsAutoCompleteAdapter = new AutocompleteGroupAdapter(JoinGroupActivity.this, SupportGroup.class, R.layout.single_autocomplete_group_item,
                AutoCompleteGroupViewHolder.class, autoSearchQuery, mEncodedEmail);
        mRecyclerViewAutocomplete.setAdapter(mGroupsAutoCompleteAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
                /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
