package com.sparshik.yogicapple.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sparshik.yogicapple.R;

/**
 * ViewHolder for chat messages
 */

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {
    public TextView mMessageBodyTextViewIncoming, mMessageHeaderTextViewIncoming, mMessageBodyTextViewOutgoing, mMessageHeaderTextViewOutgoing;
    public ImageView mUserChatProfilePicIncoming, mUserChatProfilePicOutgoing;
    public LinearLayout mIncomingContainer;
    public RelativeLayout mOutgoingContainer;

    public ChatMessageViewHolder(View itemView) {
        super(itemView);
        mMessageBodyTextViewIncoming = (TextView) itemView.findViewById(R.id.text_view_chat_message);
        mMessageBodyTextViewOutgoing = (TextView) itemView.findViewById(R.id.text_view_outgoing_chat_message);

        mMessageHeaderTextViewIncoming = (TextView) itemView.findViewById(R.id.text_view_chat_header);
        mMessageHeaderTextViewOutgoing = (TextView) itemView.findViewById(R.id.text_view_outgoing_chat_header);

        mUserChatProfilePicIncoming = (ImageView) itemView.findViewById(R.id.image_view_user_chat_pic);
        mUserChatProfilePicOutgoing = (ImageView) itemView.findViewById(R.id.image_view_outgoing_chat_pic);

        mIncomingContainer = (LinearLayout) itemView.findViewById(R.id.incoming_container);
        mOutgoingContainer = (RelativeLayout) itemView.findViewById(R.id.outgoing_container);
    }
}