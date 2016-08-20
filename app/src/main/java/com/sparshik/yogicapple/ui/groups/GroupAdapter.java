package com.sparshik.yogicapple.ui.groups;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.SupportGroup;
import com.sparshik.yogicapple.ui.groups.GroupAdapter.GroupViewHolder;
import com.sparshik.yogicapple.utils.Constants;


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

        viewHolder.mGroupDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mActivity, "Delete", Toast.LENGTH_SHORT).show();

            }
        });
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

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView mGroupImageIcon;
        TextView mTextViewGroupName, mTextViewGroupMembers;
        ImageButton mGroupDelete;
        LinearLayout mGroupItemContainer;

        public GroupViewHolder(View itemView) {
            super(itemView);
            mTextViewGroupName = (TextView) itemView.findViewById(R.id.group_name);
            mGroupImageIcon = (ImageView) itemView.findViewById(R.id.iv_group_image);
            mGroupDelete = (ImageButton) itemView.findViewById(R.id.button_group_delete);
            mGroupItemContainer = (LinearLayout) itemView.findViewById(R.id.single_group_container);
            mTextViewGroupMembers = (TextView) itemView.findViewById(R.id.group_members);
        }
    }
}
