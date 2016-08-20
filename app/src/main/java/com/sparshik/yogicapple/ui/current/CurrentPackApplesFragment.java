package com.sparshik.yogicapple.ui.current;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.sparshik.yogicapple.services.DownloadService;
import com.sparshik.yogicapple.utils.ColorUtils;
import com.sparshik.yogicapple.utils.Constants;

/**
 * Show current list of audio completed
 */
public class CurrentPackApplesFragment extends Fragment {
    private static final String LOG_TAG = CurrentPackApplesFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";
    private String mEncodedEmail, mInstallId, mCurrentProgramId, mCurrentPackId;
    private RecyclerView mRecycleView;
    private int mPosition = RecyclerView.NO_POSITION;
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mInstallId = preferences.getString(Constants.KEY_INSTALL_ID, null);
        mCurrentProgramId = preferences.getString(Constants.KEY_CURRENT_PROGRAM_ID, null);
        mCurrentPackId = preferences.getString(Constants.KEY_CURRENT_PACK_ID, null);

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen(rootView);

        //Initiating ListView

        populateFragmentList();


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

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


    public void populateFragmentList() {

//        Log.d(LOG_TAG, "Testing" + mCurrentProgramId + mCurrentPackId);

        //Updating Header Pack Information
        mPackRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_PROGRAM_PACKS).child(mCurrentProgramId).child(mCurrentPackId);
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
                            .getReferenceFromUrl(Constants.FIREBASE_URL_PACK_APPLES).child(mCurrentPackId);
                    Query orderdApplesListRef =
                            applesListRef.orderByChild(Constants.FIREBASE_PROPERTY_APPLE_SEQ_NUMBER);

                    mCurrentPackAppleRecyclerAdapter = new CurrentPackApplesRecyclerAdapter(getContext(), PackApple.class, R.layout.list_single_item_apple,
                            CurrentPackApplesViewHolder.class, orderdApplesListRef, mCurrentPackId, mCurrentProgramId, mEncodedEmail, mPackColor, mInstallId);


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


}
