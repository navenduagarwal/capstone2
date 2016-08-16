package com.sparshik.yogicapple.ui.current;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.PackApple;
import com.sparshik.yogicapple.model.UserApplesStatus;
import com.sparshik.yogicapple.services.DownloadService;
import com.sparshik.yogicapple.utils.ColorUtils;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.views.CircleProgressBar;

/**
 * Firebase Recycler Adapter to populate current list of apples
 */
public class CurrentFragmentListAdapter extends FirebaseListAdapter<PackApple> {
    private static final String LOG_TAG = CurrentFragmentListAdapter.class.getSimpleName();
    private String mPackId, mProgramId, mEncodedEmail;
    private BroadcastReceiver mDownloadReceiver;
    private int mPackColor;

    public CurrentFragmentListAdapter(Activity activity, Class<PackApple> modelClass, int modelLayout, Query ref,
                                      String packId,
                                      String programId, String encodedEmail, int packColor) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mPackId = packId;
        this.mProgramId = programId;
        this.mEncodedEmail = encodedEmail;
        this.mPackColor = packColor;
        Log.d("Color", "" + packColor);
    }

    @Override
    protected void populateView(View v, final PackApple model, int position) {
        TextView mSeqText = (TextView) v.findViewById(R.id.apple_number);
        final CircleProgressBar mProgressBar = (CircleProgressBar) v.findViewById(R.id.apple_progress_circle);
        View mLine = v.findViewById(R.id.current_line);
        ImageView mLock = (ImageView) v.findViewById(R.id.apple_locked);
        TextView mDownloadText = (TextView) v.findViewById(R.id.download_text);

        if (model.getAppleSeqNumber() < 10) {
            String sequenceText = "0" + model.getAppleSeqNumber();
            mSeqText.setText(sequenceText);
        } else {
            String sequenceText = "" + model.getAppleSeqNumber();
            mSeqText.setText(sequenceText);
        }

        final String appleId = this.getRef(position).getKey();
        final String audioUrl = model.getAudioURL();

        int altColor = ColorUtils.darkenColor(mPackColor);
        v.setBackgroundColor(mPackColor);

        mLine.setBackgroundColor(altColor);
        mDownloadText.setBackgroundColor(altColor);
        mProgressBar.setStrokeColor(altColor);
        mProgressBar.setInnerColor(altColor);
        mProgressBar.setBackgroundColor(mPackColor);
        mProgressBar.setUseRing(true);
        mLock.setColorFilter(altColor, PorterDuff.Mode.MULTIPLY);


        if (DownloadService.getDownloadProgress(appleId) != 0) {
            mProgressBar.setProgress(DownloadService.getDownloadProgress(appleId));
        }

        DatabaseReference userAppleStatusRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_USER_APPLES_STATUS)
                .child(mEncodedEmail).child(appleId);
        userAppleStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserApplesStatus userApplesStatus = dataSnapshot.getValue(UserApplesStatus.class);
                if (userApplesStatus != null) {
                    if (userApplesStatus.isOffline()) {
                        mProgressBar.setProgress(100);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, mActivity.getResources().getString(R.string.log_error_the_read_failed));
            }
        });


    }

}
