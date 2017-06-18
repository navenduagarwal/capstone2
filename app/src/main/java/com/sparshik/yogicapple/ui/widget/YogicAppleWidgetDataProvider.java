package com.sparshik.yogicapple.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.SupportGroup;
import com.sparshik.yogicapple.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * WidgetData Provider for current packs
 */

public class YogicAppleWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    protected FirebaseAuth mAuth;
    private ArrayList<SupportGroup> SGList = new ArrayList<>();
    private Context context;
    private Intent intent;
    private String mEncodedEmail, mInstallId, mCurrentProgramId, mCurrentPackId;


    public YogicAppleWidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }


    private void populateListItem() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEncodedEmail = preferences.getString(Constants.KEY_ENCODED_EMAIL, null);
        DatabaseReference userGroupsRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_USER_SUPPORT_GROUPS).child(mEncodedEmail);
//        DatabaseReference applesListRef = FirebaseDatabase.getInstance()
//                .getReferenceFromUrl(Constants.FIREBASE_URL_PACK_APPLES).child(mCurrentPackId);
        userGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    SupportGroup newSG = postSnapShot.getValue(SupportGroup.class);
                    SGList.add(0, newSG);
                    Log.d("Test 1706", "onDataChange: " + SGList.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreate() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            populateListItem();
        }
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.d("Test 1705", "onDataChange: " + SGList.size());
        if(SGList.size()>0){
            return SGList.size();
        } else {
            return 2;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d("Test 1708", "onDataChange: " + position);

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.single_widget_item);
        if (SGList.size() > 0) {
            SupportGroup newSG = SGList.get(position);
            remoteViews.setTextViewText(R.id.group_name, newSG.getGroupName());
            if (newSG.getMemberCount() < 2) {
                remoteViews.setTextViewText(R.id.group_members, context.getString(R.string.format_single_member, newSG.getMemberCount()));
            } else {
                remoteViews.setTextViewText(R.id.group_members, context.getString(R.string.format_number_of_members, newSG.getMemberCount()));
            }
            try {
                Bitmap b = Picasso.with(context).load(newSG.getGroupImageUrl()).get();
                remoteViews.setImageViewBitmap(R.id.iv_group_image, b);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Test 1709", newSG.getGroupName());
        }

//        PackApple packApple = SGList.get(position);
//        if (packApple.getAppleSeqNumber() < 10) {
//            String sequenceText = "0" + packApple.getAppleSeqNumber();
//            remoteViews.setTextViewText(R.id.apple_number, sequenceText);
//        } else {
//            String sequenceText = "" + packApple.getAppleSeqNumber();
//            remoteViews.setTextViewText(R.id.apple_number, sequenceText);
//        }
        Log.d("Test 1707", "onDataChange: " + SGList.size());
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
