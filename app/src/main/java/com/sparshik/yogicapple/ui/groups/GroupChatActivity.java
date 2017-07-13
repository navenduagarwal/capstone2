package com.sparshik.yogicapple.ui.groups;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.ChatMessage;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.ui.viewholders.ChatMessageViewHolder;
import com.sparshik.yogicapple.utils.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.sparshik.yogicapple.utils.Constants.badWords;

public class GroupChatActivity extends BaseActivity {
    private static final String LOG_TAG = GroupChatActivity.class.getSimpleName();
    private static final String MESSAGE_SENT_EVENT = "message_sent";

    private RecyclerView mRecycleViewMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMesssageEditText;
    private String mGroupId, mGroupName;
    private ImageButton mSendButton;
    private String mUserNickname, mUserProfileImageUrl;
    private GroupChatAdapter mGroupChatAdapter;
    private DatabaseReference mGroupChatRef;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chats);

        Intent intent = getIntent();
        mGroupId = intent.getStringExtra(Constants.KEY_GROUP_ID);
        mGroupName = intent.getStringExtra(Constants.KEY_GROUP_NAME);
        mUserNickname = intent.getStringExtra(Constants.KEY_CHAT_NICK_NAME);
        mUserProfileImageUrl = intent.getStringExtra(Constants.KEY_CHAT_PROFILE_IMAGE_URL);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


//        if (mGroupName == null | mGroupId == null || mUserNickname == null || mUserProfileImageUrl == null) {
//            //No point in continuing with valid group id */
//            Toast.makeText(this, "One among groupName, groupId, nickName or profileImage found", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, GroupsActivity.class));
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeScreen();

        mGroupChatAdapter = new GroupChatAdapter(GroupChatActivity.this, ChatMessage.class, R.layout.single_item_chat_message,
                ChatMessageViewHolder.class, mGroupChatRef, mEncodedEmail, mProgressBar);
        mRecycleViewMessage.setAdapter(mGroupChatAdapter);

        mGroupChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatMessageCount = mGroupChatAdapter.getItemCount();
                String title = mGroupName + " (" + chatMessageCount + ")";
                setTitle(title);
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mRecycleViewMessage.scrollToPosition(positionStart);
                }
            }
        });


        //Initialize Firebase Measurement;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        //Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        //Define default config values. Defaults are used when fetched config values are not available
        //Eg: if an error occurred fetching values from the server
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(Constants.KEY_CHAT_MESSAGE_LENGTH, Constants.DEFAULT_MSG_LENGTH_LIMIT);

        //Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        //Fetch remote config.
        fetchConfig();

        mMesssageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences.getInt(Constants.KEY_CHAT_MESSAGE_LENGTH, Constants.DEFAULT_MSG_LENGTH_LIMIT))});
        mMesssageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                    mSendButton.setBackgroundColor(getResources().getColor(R.color.teal_100));
                } else {
                    mSendButton.setEnabled(false);
                    mSendButton.setBackgroundColor(getResources().getColor(R.color.grey));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> timestampCreated = new HashMap<>();
                timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
                timestampCreated.put(Constants.FIREBASE_PROPERTY_CREATED_BY, mEncodedEmail);
                String chatText = mMesssageEditText.getText().toString();
                String chatTextUpdated = chatText;
                for (String word : badWords) {
                    Pattern rx = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
                    chatTextUpdated = rx.matcher(chatTextUpdated).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
                }
                ChatMessage newChatMessage = new ChatMessage(chatTextUpdated, mUserNickname, mUserProfileImageUrl, timestampCreated);
                mGroupChatRef.push().setValue(newChatMessage);
                mMesssageEditText.setText("");
                mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGroupChatAdapter != null) {
            mGroupChatAdapter.cleanup();
        }
    }

    public void initializeScreen() {
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mMesssageEditText = (EditText) findViewById(R.id.edit_text_chat_message);
        mRecycleViewMessage = (RecyclerView) findViewById(R.id.recycler_view_chats);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecycleViewMessage.setLayoutManager(mLinearLayoutManager);
        mGroupChatRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_GROUP_CHATS).child(mGroupId);
    }

    private void causeCrash() {
        throw new NullPointerException("Fake null pointer exception");
    }


    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(LOG_TAG, "Error fetching config: " + e.getMessage());
                        applyRetrievedLengthLimit();
                    }
                });
    }

    /**
     * Apply retrieved length limit to edit text field. This result may be fresh from the server or it may be from
     * cached values.
     */
    private void applyRetrievedLengthLimit() {
        Long chat_msg_length = mFirebaseRemoteConfig.getLong(Constants.KEY_CHAT_MESSAGE_LENGTH);
        mMesssageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(chat_msg_length.intValue())});
        Log.d(LOG_TAG, "FML is: " + chat_msg_length);
    }
}
