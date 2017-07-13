package com.sparshik.yogicapple.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sparshik.yogicapple.R;

/**
 * view holder for groups
 */

public class GroupViewHolder extends RecyclerView.ViewHolder {
    public ImageView mGroupImageIcon;
    public TextView mTextViewGroupName, mTextViewGroupMembers;
    public ImageButton mGroupDelete;
    public LinearLayout mGroupItemContainer;

    public GroupViewHolder(View itemView) {
        super(itemView);
        mTextViewGroupName = (TextView) itemView.findViewById(R.id.group_name);
        mGroupImageIcon = (ImageView) itemView.findViewById(R.id.iv_group_image);
        mGroupDelete = (ImageButton) itemView.findViewById(R.id.button_group_delete);
        mGroupItemContainer = (LinearLayout) itemView.findViewById(R.id.single_group_container);
        mTextViewGroupMembers = (TextView) itemView.findViewById(R.id.group_members);
    }
}