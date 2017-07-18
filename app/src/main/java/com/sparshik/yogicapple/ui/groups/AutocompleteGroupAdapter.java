package com.sparshik.yogicapple.ui.groups;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.SupportGroup;
import com.sparshik.yogicapple.ui.viewholders.AutoCompleteGroupViewHolder;
import com.sparshik.yogicapple.utils.Constants;

import timber.log.Timber;

/**
 * Firebase Recycler Adapter to auto populate list of groups for user
 */
public class AutocompleteGroupAdapter extends FirebaseRecyclerAdapter<SupportGroup, AutoCompleteGroupViewHolder> {
    private String mEncodedEmail;
    private Activity mActivity;

    public AutocompleteGroupAdapter(Activity activity, Class<SupportGroup> modelClass, int modelLayout, Class<AutoCompleteGroupViewHolder> viewHolderClass, Query ref, String encodedEmail) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mActivity = activity;
        this.mEncodedEmail = encodedEmail;
    }

    @Override
    protected void populateViewHolder(AutoCompleteGroupViewHolder viewHolder, final SupportGroup supportGroup, int position) {
        viewHolder.mTextViewGroupName.setText(supportGroup.getGroupName());

        final String supportGroupId = this.getRef(position).getKey();
        DatabaseReference supportGroupRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_SUPPORT_GROUPS).child(supportGroupId);



        viewHolder.mTextViewGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference currentUserGroupsRef = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl(Constants.FIREBASE_URL_USER_SUPPORT_GROUPS).child(mEncodedEmail);
                final DatabaseReference userSupportGroupsRef = currentUserGroupsRef.child(supportGroupId);
                /**
                 * Add listener for single value event to perform a one time operation
                 */
                userSupportGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (isNotAlreadyAdded(dataSnapshot, supportGroup)) {
                            userSupportGroupsRef.setValue(supportGroup);
                            mActivity.startActivity(new Intent(mActivity, GroupsActivity.class));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e(mActivity.getResources().getString(R.string.log_error_the_read_failed) + databaseError.getMessage());
                    }
                });
            }
        });
    }

    public boolean isNotAlreadyAdded(DataSnapshot dataSnapshot, SupportGroup supportGroup) {
        if (dataSnapshot.getValue(SupportGroup.class) != null) {
            /* Toast appropriate error message if the supportGroup is already joined by the user */
            String supportGroupError = String.format(mActivity.getResources().
                            getString(R.string.toast_is_already_your_group),
                    supportGroup.getGroupName());
            Toast.makeText(mActivity, supportGroupError, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
