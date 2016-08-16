package com.sparshik.yogicapple.ui.current;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.sparshik.yogicapple.model.UserApplesStatus;
import com.sparshik.yogicapple.services.DownloadService;
import com.sparshik.yogicapple.ui.current.CurrentPackApplesFragment.PackApplesHolder;
import com.sparshik.yogicapple.utils.ColorUtils;
import com.sparshik.yogicapple.utils.Constants;

/**
 * Recycler Adapter to populate list of apples for current program
 */
public class CurrentPackApplesRecyclerAdapter extends FirebaseRecyclerAdapter<PackApple, PackApplesHolder> {
    private static final String LOG_TAG = CurrentPackApplesRecyclerAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private String mPackId, mProgramId, mEncodedEmail;
    private BroadcastReceiver mDownloadReceiver;
    private int mPackColor;
    private Context context;

    public CurrentPackApplesRecyclerAdapter(Context context, Class<PackApple> modelClass, int modelLayout, Class<PackApplesHolder> viewHolderClass, Query ref,
                                            String mPackId, String mProgramId, String mEncodedEmail, int mPackColor) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mPackId = mPackId;
        this.mProgramId = mProgramId;
        this.mEncodedEmail = mEncodedEmail;
        this.mPackColor = mPackColor;
        this.context = context;
    }

    @Override
    protected void populateViewHolder(final PackApplesHolder viewHolder, PackApple model, int position) {

        if (model.getAppleSeqNumber() < 10) {
            String sequenceText = "0" + model.getAppleSeqNumber();
            viewHolder.setSeqText(sequenceText);
        } else {
            String sequenceText = "" + model.getAppleSeqNumber();
            viewHolder.setSeqText(sequenceText);
        }


        final String appleId = this.getRef(position).getKey();
        final String audioUrl = model.getAudioURL();

        int altColor = ColorUtils.darkenColor(mPackColor);
        viewHolder.setBackGroundColor(mPackColor);
        viewHolder.setLineColor(altColor);
        viewHolder.setDownloadTextColor(altColor);
        viewHolder.setCircleColor(mPackColor);
        viewHolder.setLockColor(altColor);

        if (DownloadService.getDownloadProgress(appleId) != 0) {
            viewHolder.setProgress(DownloadService.getDownloadProgress(appleId));
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
                        viewHolder.setProgress(100);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, context.getResources().getString(R.string.log_error_the_read_failed));
            }
        });

    }

    @Override
    public PackApplesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_empty, parent, false);
            return new FooterViewHolder(v);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    class FooterViewHolder extends PackApplesHolder {
        TextView txtTitleFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            this.txtTitleFooter = (TextView) itemView.findViewById(R.id.text_view_listview_footer);
        }
    }
}
