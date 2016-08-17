package com.sparshik.yogicapple.ui.current;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.PackApple;
import com.sparshik.yogicapple.model.User;
import com.sparshik.yogicapple.model.UserOfflineDownloads;
import com.sparshik.yogicapple.services.DownloadService;
import com.sparshik.yogicapple.ui.player.ExoPlayerActivity;
import com.sparshik.yogicapple.utils.ColorUtils;
import com.sparshik.yogicapple.utils.Constants;

import java.io.File;

/**
 * Recycler Adapter to populate list of apples for current program
 */
public class CurrentPackApplesRecyclerAdapter extends FirebaseRecyclerAdapter<PackApple, CurrentPackApplesViewHolder> {
    private static final String LOG_TAG = CurrentPackApplesRecyclerAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private String mPackId, mProgramId, mEncodedEmail;
    private BroadcastReceiver mDownloadReceiver;
    private int mPackColor;
    private Context context;
    private String mInstallId;

    public CurrentPackApplesRecyclerAdapter(Context context, Class<PackApple> modelClass, int modelLayout, Class<CurrentPackApplesViewHolder> viewHolderClass, Query ref,
                                            String mPackId, String mProgramId, String mEncodedEmail, int mPackColor, String installId) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mPackId = mPackId;
        this.mProgramId = mProgramId;
        this.mEncodedEmail = mEncodedEmail;
        this.mPackColor = mPackColor;
        this.context = context;
        this.mInstallId = installId;
    }

    @Override
    protected void populateViewHolder(final CurrentPackApplesViewHolder viewHolder, PackApple model, int position) {

        if (model.getAppleSeqNumber() < 10) {
            String sequenceText = "0" + model.getAppleSeqNumber();
            viewHolder.mSeqNumberText.setText(sequenceText);
        } else {
            String sequenceText = "" + model.getAppleSeqNumber();
            viewHolder.mSeqNumberText.setText(sequenceText);
        }


        final String appleId = this.getRef(position).getKey();
        final String audioUrl = model.getAudioURL();

        int altColor = ColorUtils.darkenColor(mPackColor);
        viewHolder.mLineView.setBackgroundColor(altColor);
        viewHolder.mContainer.setBackgroundColor(mPackColor);
        viewHolder.mProgressBar.setBackgroundColor(mPackColor);
        viewHolder.mProgressBar.setInnerColor(altColor);
        viewHolder.mProgressBar.setStrokeColor(altColor);
        viewHolder.mProgressBar.setUseRing(true);
        viewHolder.mAppleState.setColorFilter(altColor, PorterDuff.Mode.MULTIPLY);
        viewHolder.mDownloadText.setTextColor(altColor);

        if (DownloadService.getDownloadProgress(appleId) != 0) {
            viewHolder.mProgressBar.setProgress(DownloadService.getDownloadProgress(appleId));
            viewHolder.mDownloadText.setText(context.getResources().getString(R.string.download_apple_progress));
        }

        DatabaseReference userAppleStatusRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_USER_OFFLINE_DOWNLOADS)
                .child(mEncodedEmail).child(mInstallId).child(appleId);
        userAppleStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserOfflineDownloads userOfflineDownloads = dataSnapshot.getValue(UserOfflineDownloads.class);
                if (userOfflineDownloads != null) {
                    File file = new File(userOfflineDownloads.getLocalAudioFile());
                    if (file.exists()) {
                        viewHolder.mProgressBar.setProgress(100);
                        viewHolder.mDownloadText.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, context.getResources().getString(R.string.log_error_the_read_failed));
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("testing adapter", DownloadService.getDownloadProgress(appleId) + "");
                if (DownloadService.getDownloadProgress(appleId) == 0) {
                    DatabaseReference userAppleStatusRef = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl(Constants.FIREBASE_URL_USER_OFFLINE_DOWNLOADS)
                            .child(mEncodedEmail).child(mInstallId).child(appleId);

                    userAppleStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserOfflineDownloads userOfflineDownloads = dataSnapshot.getValue(UserOfflineDownloads.class);
                            if (userOfflineDownloads != null) {
                                File file = new File(userOfflineDownloads.getLocalAudioFile());
                                if (file.exists()) {
                                    startPlayer(appleId, userOfflineDownloads);
                                } else {
                                    viewHolder.mProgressBar.setProgress(0);
                                    downloadAppleFiles(appleId, audioUrl);
                                    viewHolder.mDownloadText.setVisibility(View.VISIBLE);
                                    viewHolder.mDownloadText.setText(context.getResources().getString(R.string.download_apple_prepare));
                                }
                            } else {
                                viewHolder.mProgressBar.setProgress(0);
                                downloadAppleFiles(appleId, audioUrl);
                                viewHolder.mDownloadText.setVisibility(View.VISIBLE);
                                viewHolder.mDownloadText.setText(context.getResources().getString(R.string.download_apple_prepare));
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(LOG_TAG, context.getResources().getString(R.string.log_error_the_read_failed));
                        }
                    });
                } else {
                    Log.d("Testing what", "Got stucked");
                }
            }
        });

    }

    @Override
    public CurrentPackApplesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_empty, parent, false);
            return new FooterViewViewHolderCurrent(v);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    public void downloadAppleFiles(String appleId, String audioUrl) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(DownloadService.ACTION_DOWNLOAD);
        intent.putExtra(DownloadService.EXTRA_APPLE_ID, appleId);
        intent.putExtra(DownloadService.EXTRA_DOWNLOAD_PATH, audioUrl);
        intent.putExtra(DownloadService.EXTRA_FILE_SUFFIX, Constants.SUFFIX_AUDIO);
        intent.putExtra(DownloadService.EXTRA_ENCODED_EMAIL, mEncodedEmail);
        context.startService(intent);
    }

    public void startPlayer(final String appleId, final UserOfflineDownloads userOfflineDownloads) {


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userData = dataSnapshot.getValue(User.class);
                if (userData != null) {
                    String programId = userData.getCurrentProgramId();
                    String packId = userData.getCurrentPackId();

                    // Start Audio Player
                    Intent intent = new Intent(context, ExoPlayerActivity.class);
                    intent.putExtra(Constants.KEY_APPLE_ID, appleId);
                    intent.putExtra(Constants.KEY_PACK_ID, packId);
                    intent.putExtra(Constants.KEY_PROGRAM_ID, programId);
                    intent.putExtra(Constants.KEY_AUDIO_URL, userOfflineDownloads.getLocalAudioFile());
                    /* Start an activity showing the packs for selected program */
                    Log.d("To Player", packId + " " + appleId);
                    context.startActivity(intent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, context.getResources().getString(R.string.log_error_the_read_failed));
            }
        });
    }

    class FooterViewViewHolderCurrent extends CurrentPackApplesViewHolder {
        TextView txtTitleFooter;

        public FooterViewViewHolderCurrent(View itemView) {
            super(itemView);
            this.txtTitleFooter = (TextView) itemView.findViewById(R.id.text_view_listview_footer);
        }
    }
}

