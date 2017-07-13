package com.sparshik.yogicapple.ui.groups;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.SupportGroup;
import com.sparshik.yogicapple.ui.viewholders.GroupViewHolder;
import com.sparshik.yogicapple.utils.Constants;

import java.util.HashMap;


/**
 * Populates list of joined groups for given user
 */
public class GroupAdapter extends FirebaseRecyclerAdapter<SupportGroup, GroupViewHolder> {
    private Activity mActivity;
    private String mEncodedEmail, mUserChatName, mUserChatProfileUrl;

    public GroupAdapter(Activity activity, Class<SupportGroup> modelClass
            , int modelLayout, Class<GroupViewHolder> viewHolderClass, Query ref, String encodedEmail, String userChatName, String userProfileUrl) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mActivity = activity;
        this.mEncodedEmail = encodedEmail;
        this.mUserChatName = userChatName;
        this.mUserChatProfileUrl = userProfileUrl;
    }

    @Override
    protected void populateViewHolder(final GroupViewHolder viewHolder, SupportGroup supportGroup, int position) {

        Log.d("GroupAdapter", mEncodedEmail);

        final String groupId = this.getRef(position).getKey();
        final String groupName = supportGroup.getGroupName();

        viewHolder.mTextViewGroupName.setText(supportGroup.getGroupName());
        if (supportGroup.getMemberCount() < 2) {
            viewHolder.mTextViewGroupMembers.setText(mActivity.getString(R.string.format_single_member, supportGroup.getMemberCount()));
        } else {
            viewHolder.mTextViewGroupMembers.setText(mActivity.getString(R.string.format_number_of_members, supportGroup.getMemberCount()));
        }
        if (!TextUtils.isEmpty(supportGroup.getGroupImageUrl())) {
            Glide.with(mActivity).load(supportGroup.getGroupImageUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(viewHolder.mGroupImageIcon) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mActivity.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    viewHolder.mGroupImageIcon.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            viewHolder.mGroupImageIcon.setVisibility(View.INVISIBLE);
        }

        viewHolder.mGroupItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                checkForChatProfile(groupId, groupName);
                Intent intentChat = new Intent(mActivity, GroupChatActivity.class);
                intentChat.putExtra(Constants.KEY_GROUP_ID, groupId);
                intentChat.putExtra(Constants.KEY_GROUP_NAME, groupName);
                intentChat.putExtra(Constants.KEY_CHAT_NICK_NAME, mUserChatName);
                intentChat.putExtra(Constants.KEY_CHAT_PROFILE_IMAGE_URL, mUserChatProfileUrl);
                mActivity.startActivity(intentChat);

            }
        });

        final String groupToRemoveId = this.getRef(position).getKey();
        viewHolder.mGroupDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity, R.style.CustomTheme_Dialog)
                        .setTitle(mActivity.getString(R.string.remove_selected_group_option))
                        .setMessage(mActivity.getString(R.string.dialog_message_are_you_sure_remove_group))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (groupToRemoveId != null) {
                                    removeGroup(groupToRemoveId);
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /* Dismiss the dialog */
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void removeGroup(String groupId) {
        DatabaseReference userGroupsRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_USER_SUPPORT_GROUPS).child(mEncodedEmail);
        HashMap<String, Object> removeGroup = new HashMap<String, Object>();
        removeGroup.put("/" + groupId, null);
        userGroupsRef.updateChildren(removeGroup);
    }

    /**
     * method checks if user have chat nick name, if not request him to set one, also download random photo from Unsplash
     */
//    public void checkForChatProfile(final String groupId, final String groupName) {
//
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
//        String userNickname = sharedPreferences.getString(Constants.KEY_CHAT_NICK_NAME, null);
//        String userProfileImageUrl = sharedPreferences.getString(Constants.KEY_CHAT_PROFILE_IMAGE_URL, null);
//
//        if (userNickname != null && userProfileImageUrl != null) {
//            DatabaseReference chatProfileRef = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL_GROUP_CHAT_PROFILES).child(mEncodedEmail);
//            chatProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    UserChatProfile userChatProfile = dataSnapshot.getValue(UserChatProfile.class);
//                    if (userChatProfile != null) {
//                        Intent intentChat = new Intent(mActivity, GroupChatActivity.class);
//                        intentChat.putExtra(Constants.KEY_GROUP_ID, groupId);
//                        intentChat.putExtra(Constants.KEY_GROUP_NAME, groupName);
//                        mActivity.startActivity(intentChat);
//                    } else {
//                        Intent intentCreate = new Intent(mActivity, CreateChatProfileActivity.class);
//                        intentCreate.putExtra(Constants.KEY_GROUP_ID, groupId);
//                        intentCreate.putExtra(Constants.KEY_GROUP_NAME, groupName);
//                        mActivity.startActivity(intentCreate);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.e(mActivity.getClass().getSimpleName(), mActivity.getResources().getString(R.string.log_error_the_read_failed) + databaseError.getMessage());
//                }
//            });
//        } else {
//            Intent intentCreate = new Intent(mActivity, CreateChatProfileActivity.class);
//            intentCreate.putExtra(Constants.KEY_GROUP_ID, groupId);
//            intentCreate.putExtra(Constants.KEY_GROUP_NAME, groupName);
//            mActivity.startActivity(intentCreate);
//        }
//    }

}
