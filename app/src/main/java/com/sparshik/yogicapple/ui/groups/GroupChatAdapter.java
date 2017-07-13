package com.sparshik.yogicapple.ui.groups;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.ChatMessage;
import com.sparshik.yogicapple.ui.viewholders.ChatMessageViewHolder;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.utils.DateUtils;

/**
 * Adapter to populate single message item
 */
public class GroupChatAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> {
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
        String formattedChatMessageHeader;
        long dateInMills = Long.parseLong(chatMessage.getTimestampCreated().get(Constants.FIREBASE_PROPERTY_TIMESTAMP).toString());

        String formattedTimeStamp = DateUtils.getChatTimeStamp(mActivity, dateInMills);
        Log.d("Chat Adapter", formattedTimeStamp);
        if (chatMessage.getTimestampCreated().get(Constants.FIREBASE_PROPERTY_CREATED_BY) != null) {
            chatCreatorEmail = chatMessage.getTimestampCreated().get(Constants.FIREBASE_PROPERTY_CREATED_BY).toString();
        }
        String chatCreatorName = chatMessage.getNickName();

        if (chatCreatorEmail.equals(mEncodedEmail)) {
            viewHolder.mOutgoingContainer.setVisibility(View.VISIBLE);
            viewHolder.mIncomingContainer.setVisibility(View.GONE);


            Log.d("Testing", chatCreatorName + chatCreatorEmail + mEncodedEmail);

            //Set body
            viewHolder.mMessageBodyTextViewOutgoing.setText(chatMessage.getText());

            //Set header
            formattedChatMessageHeader = formattedTimeStamp;
            viewHolder.mMessageHeaderTextViewOutgoing.setText(formattedChatMessageHeader);

            //set Image
            if (chatMessage.getUserProfilePicUrl() != null) {
                Glide.with(mActivity).load(chatMessage.getUserProfilePicUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(viewHolder.mUserChatProfilePicOutgoing) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mActivity.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        viewHolder.mUserChatProfilePicOutgoing.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } else {
                viewHolder.mUserChatProfilePicOutgoing.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.chat_image_2));
            }




        } else {
            viewHolder.mOutgoingContainer.setVisibility(View.GONE);
            viewHolder.mIncomingContainer.setVisibility(View.VISIBLE);

            formattedChatMessageHeader = chatCreatorName + " - " + formattedTimeStamp;
            viewHolder.mMessageBodyTextViewIncoming.setText(chatMessage.getText());

            if (chatMessage.getUserProfilePicUrl() != null) {
                Glide.with(mActivity).load(chatMessage.getUserProfilePicUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(viewHolder.mUserChatProfilePicIncoming) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mActivity.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        viewHolder.mUserChatProfilePicIncoming.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } else {
                viewHolder.mUserChatProfilePicIncoming.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.chat_image_5));
            }
            viewHolder.mMessageBodyTextViewIncoming.setBackground(mActivity.getResources().getDrawable(R.drawable.shape_bg_incoming_bubble));
            viewHolder.mMessageHeaderTextViewIncoming.setText(formattedChatMessageHeader);
        }
    }
}
