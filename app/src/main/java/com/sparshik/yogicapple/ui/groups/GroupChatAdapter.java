package com.sparshik.yogicapple.ui.groups;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    protected void populateViewHolder(ChatMessageViewHolder viewHolder, ChatMessage chatMessage, int position) {
        viewHolder.mMessageBodyTextView.setText(chatMessage.getText());
        long dateInmills = Long.parseLong(chatMessage.getTimestampCreated().get(Constants.FIREBASE_PROPERTY_TIMESTAMP).toString());
        String formattedTimeStamp = DateUtils.getChatTimeStamp(mActivity, dateInmills);
        viewHolder.mMessageHeaderTextView.setText(formattedTimeStamp);

    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        TextView mMessageBodyTextView, mMessageHeaderTextView;

        public ChatMessageViewHolder(View itemView) {
            super(itemView);
            mMessageBodyTextView = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            mMessageHeaderTextView = (TextView) itemView.findViewById(R.id.text_view_chat_header);
        }
    }
}
