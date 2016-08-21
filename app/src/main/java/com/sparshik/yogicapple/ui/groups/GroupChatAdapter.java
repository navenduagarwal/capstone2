package com.sparshik.yogicapple.ui.groups;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.ChatMessage;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.utils.DateUtils;

/**
 * Adaptr to populate single message item
 */
public class GroupChatAdapter extends FirebaseRecyclerAdapter<ChatMessage, GroupChatAdapter.ChatMessageViewHolder> {
    private ProgressBar mProgressBar;
    private String mEncodedEmail;
    private Activity mActivity;

    public GroupChatAdapter(Activity activity, Class<ChatMessage> modelClass, int modelLayout,
                            Class<ChatMessageViewHolder> viewHolderClass, Query ref, String encodedEmail, ProgressBar progressBar) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mActivity = activity;
        this.mEncodedEmail = encodedEmail;
        this.mProgressBar = progressBar;
    }

    @Override
    protected void populateViewHolder(final ChatMessageViewHolder viewHolder, ChatMessage chatMessage, int position) {
        String chatCreatorEmail = null;
        viewHolder.mMessageBodyTextView.setText(chatMessage.getText());
        long dateInMills = Long.parseLong(chatMessage.getTimestampCreated().get(Constants.FIREBASE_PROPERTY_TIMESTAMP).toString());
        if (chatMessage.getTimestampCreated().get(Constants.FIREBASE_PROPERTY_CREATED_BY) != null) {
            chatCreatorEmail = chatMessage.getTimestampCreated().get(Constants.FIREBASE_PROPERTY_CREATED_BY).toString();
        }
        String chatCreatorName = chatMessage.getNickName();
        String formattedTimeStamp = DateUtils.getChatTimeStamp(mActivity, dateInMills);
        String formattedChatMessageHeader = chatCreatorName + " - " + formattedTimeStamp;
        viewHolder.mMessageHeaderTextView.setText(formattedChatMessageHeader);

        if (chatMessage.getUserProfilePicUrl() != null) {
            Glide.with(mActivity).load(chatMessage.getUserProfilePicUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(viewHolder.mUserChatProfilePic) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mActivity.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    viewHolder.mUserChatProfilePic.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            viewHolder.mUserChatProfilePic.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.chat_image_2));
        }


        if (chatCreatorEmail.equals(mEncodedEmail)) {
            Log.d("Testing", chatCreatorName + chatCreatorEmail + mEncodedEmail);
            viewHolder.mMessageBodyTextView.setBackground(mActivity.getResources().getDrawable(R.drawable.chat_message_shadow_dark));
        } else {
            viewHolder.mMessageBodyTextView.setBackground(mActivity.getResources().getDrawable(R.drawable.chat_message_shadow_grey));
        }
    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        TextView mMessageBodyTextView, mMessageHeaderTextView;
        ImageView mUserChatProfilePic;

        public ChatMessageViewHolder(View itemView) {
            super(itemView);
            mMessageBodyTextView = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            mMessageHeaderTextView = (TextView) itemView.findViewById(R.id.text_view_chat_header);
            mUserChatProfilePic = (ImageView) itemView.findViewById(R.id.image_view_user_chat_pic);
        }
    }
}
