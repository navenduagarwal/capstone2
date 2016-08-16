package com.sparshik.yogicapple.ui.current;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.Pack;
import com.sparshik.yogicapple.model.PackApple;
import com.sparshik.yogicapple.model.User;
import com.sparshik.yogicapple.model.UserApplesStatus;
import com.sparshik.yogicapple.services.DownloadService;
import com.sparshik.yogicapple.ui.player.ExoPlayerActivity;
import com.sparshik.yogicapple.utils.ColorUtils;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.views.CircleProgressBar;

/**
 * Show current list of audio completed
 */
public class CurrentPackApplesFragment extends Fragment {
    private static final String LOG_TAG = CurrentPackApplesFragment.class.getSimpleName();
    private String mEncodedEmail;
    private ListView mListView;
    private RecyclerView mRecycleView;
    private CurrentPackApplesRecyclerAdapter mCurrentPackAppleRecyclerAdapter;
    private TextView mTextViewHeaderTitle, mTextViewHeaderBody;
    private ImageView mImageViewHeaderIcon, mImageViewHeaderPackImage;
    private Pack mPack;
    private DatabaseReference mPackRef;
    private BroadcastReceiver mDownloadReceiver;
    private LinearLayout mTopContainer;

    public CurrentPackApplesFragment() {
        super();
    }

    /**
     * Create fragment and pass bundle with data as its' arguments
     */
    public static CurrentPackApplesFragment newInstance(String encodedEmail) {
        CurrentPackApplesFragment fragment = new CurrentPackApplesFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_ENCODED_EMAIL, encodedEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEncodedEmail = this.getArguments().getString(Constants.KEY_ENCODED_EMAIL);


        mDownloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "downloadReceiver:onReceive:" + intent);

                if (DownloadService.ACTION_COMPLETED.equals(intent.getAction())) {
                    String path = intent.getStringExtra(DownloadService.EXTRA_DOWNLOAD_PATH);
                    long numBytes = intent.getLongExtra(DownloadService.EXTRA_BYTES_DOWNLOADED, 0);
                    mCurrentPackAppleRecyclerAdapter.notifyDataSetChanged();
                    // Alert success
                }

                if (DownloadService.ACTION_PROGRESS.equals(intent.getAction())) {
                    mCurrentPackAppleRecyclerAdapter.notifyDataSetChanged();
                }

                if (DownloadService.ACTION_ERROR.equals(intent.getAction())) {
                    String path = intent.getStringExtra(DownloadService.EXTRA_DOWNLOAD_PATH);
                    // Alert failure
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View rootView = inflater.inflate(R.layout.fragment_current, container, false);


        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen(rootView);

        /**
         * Set interactive bits, such as click events/adapters
         *
         */

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PackApple packApple = mCurrentPackAppleRecyclerAdapter.getItem(position);
//                if (packApple != null) {
//                    final String appleId = mCurrentPackAppleRecyclerAdapter.getRef(position).getKey();
//                    final String audioUrl = packApple.getAudioURL();
//                    //check for apple offline status
//
//                    DatabaseReference userAppleStatusRef = FirebaseDatabase.getInstance()
//                            .getReferenceFromUrl(Constants.FIREBASE_URL_USER_APPLES_STATUS)
//                            .child(mEncodedEmail).child(appleId);
//
//                    userAppleStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            UserApplesStatus userApplesStatus = dataSnapshot.getValue(UserApplesStatus.class);
//                            if (userApplesStatus != null) {
//
//                                File file = new File(userApplesStatus.getLocalAudioFile());
//
//                                if (!userApplesStatus.isOffline() || !file.exists()) {
//                                    downloadAppleFiles(appleId, audioUrl);
//                                    mCurrentPackAppleRecyclerAdapter.notifyDataSetChanged();
//                                } else {
//                                    startPlayer(appleId, userApplesStatus);
//                                }
//                            } else {
//                                downloadAppleFiles(appleId, audioUrl);
//                                mCurrentPackAppleRecyclerAdapter.notifyDataSetChanged();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Initiating ListView

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userData = dataSnapshot.getValue(User.class);
                if (userData != null) {
                    String programId = userData.getDefaultProgramId();
                    String packId = userData.getDefaultPackId();
                    populateListView(programId, packId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, getResources().getString(R.string.log_error_the_read_failed));
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCurrentPackAppleRecyclerAdapter != null) {
            mCurrentPackAppleRecyclerAdapter.cleanup();
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mDownloadReceiver);
    }

    private void initializeScreen(View view) {
        View footer = getActivity().getLayoutInflater().inflate(R.layout.footer_empty, null);
        mTextViewHeaderTitle = (TextView) view.findViewById(R.id.text_view_title_header);
        mTextViewHeaderBody = (TextView) view.findViewById(R.id.text_view_body_header);
        mImageViewHeaderIcon = (ImageView) view.findViewById(R.id.image_view_icon_header);
        mImageViewHeaderPackImage = (ImageView) view.findViewById(R.id.image_view_pack_image_header);
        mTopContainer = (LinearLayout) view.findViewById(R.id.header_container);
        mRecycleView = (RecyclerView) view.findViewById(R.id.recycle_view_current_pack);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void downloadAppleFiles(String appleId, String audioUrl) {
        Intent intent = new Intent(getActivity(), DownloadService.class);
        intent.setAction(DownloadService.ACTION_DOWNLOAD);
        intent.putExtra(DownloadService.EXTRA_APPLE_ID, appleId);
        intent.putExtra(DownloadService.EXTRA_DOWNLOAD_PATH, audioUrl);
        intent.putExtra(DownloadService.EXTRA_FILE_SUFFIX, Constants.SUFFIX_AUDIO);
        intent.putExtra(DownloadService.EXTRA_ENCODED_EMAIL, mEncodedEmail);
        getActivity().startService(intent);
    }

    public void populateListView(final String programId, final String packId) {

        //Updating Header Pack Information
        mPackRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_PROGRAM_PACKS).child(programId).child(packId);
        mPackRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pack pack = dataSnapshot.getValue(Pack.class);
                if (pack != null) {
                    mPack = pack;
                    mTextViewHeaderTitle.setText(pack.getPackTitle());
                    mTextViewHeaderBody.setText(pack.getPackDesc());

                    int mPackColor = getResources().getColor(R.color.primary);  //pack.getPackColorInt();
                    int backgroundColor = ColorUtils.darkenColor(mPackColor); //TODO
                    mTopContainer.setBackgroundColor(backgroundColor);

                    /** Create Firebase Ref **/
                    DatabaseReference applesListRef = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl(Constants.FIREBASE_URL_PACK_APPLES).child(packId);
                    Query orderdApplesListRef =
                            applesListRef.orderByChild(Constants.FIREBASE_PROPERTY_APPLE_SEQ_NUMBER);

                    mCurrentPackAppleRecyclerAdapter = new CurrentPackApplesRecyclerAdapter(getContext(), PackApple.class, R.layout.list_single_item_apple,
                            PackApplesHolder.class, orderdApplesListRef, packId, programId, mEncodedEmail, mPackColor);

                    mRecycleView.setAdapter(mCurrentPackAppleRecyclerAdapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(getActivity().getClass().getSimpleName(),
                        getResources().getString(R.string.log_error_the_read_failed)
                                + databaseError.getMessage());
            }
        });


        // Register download receiver
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mDownloadReceiver, DownloadService.getIntentFilter());
    }

    public void startPlayer(final String appleId, final UserApplesStatus userApplesStatus) {


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userData = dataSnapshot.getValue(User.class);
                if (userData != null) {
                    String programId = userData.getDefaultProgramId();
                    String packId = userData.getDefaultPackId();

                    // Start Audio Player
                    Intent intent = new Intent(getActivity(), ExoPlayerActivity.class);
                    intent.putExtra(Constants.KEY_APPLE_ID, appleId);
                    intent.putExtra(Constants.KEY_PACK_ID, packId);
                    intent.putExtra(Constants.KEY_PROGRAM_ID, programId);
                    intent.putExtra(Constants.KEY_AUDIO_URL, userApplesStatus.getLocalAudioFile());
                    /* Start an activity showing the packs for selected program */
                    Log.d("To Player", packId + " " + appleId);
                    getActivity().startActivity(intent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, getResources().getString(R.string.log_error_the_read_failed));
            }
        });
    }

    public static class PackApplesHolder extends RecyclerView.ViewHolder {
        View mView;

        public PackApplesHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setBackGroundColor(int color) {
            RelativeLayout field = (RelativeLayout) mView.findViewById(R.id.container_list_item);
            field.setBackgroundColor(color);
        }

        public void setSeqText(String seq) {
            TextView field = (TextView) mView.findViewById(R.id.apple_number);
            field.setText(seq);
        }

        public void setProgress(int progress) {
            CircleProgressBar field = (CircleProgressBar) mView.findViewById(R.id.apple_progress_circle);
            field.setProgress(progress);
        }

        public void setCircleColor(int color) {
            int altColor = ColorUtils.darkenColor(color);
            CircleProgressBar field = (CircleProgressBar) mView.findViewById(R.id.apple_progress_circle);
            field.setBackgroundColor(color);
            field.setInnerColor(altColor);
            field.setStrokeColor(altColor);
            field.setUseRing(true);
        }

        public void setLineColor(int color) {
            View field = mView.findViewById(R.id.current_line);
            field.setBackgroundColor(color);
        }

        public void setLockColor(int color) {
            ImageView field = (ImageView) mView.findViewById(R.id.apple_locked);
            field.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }

        public void setDownloadTextColor(int color) {
            TextView field = (TextView) mView.findViewById(R.id.download_text);
            field.setBackgroundColor(color);
        }

    }

}
