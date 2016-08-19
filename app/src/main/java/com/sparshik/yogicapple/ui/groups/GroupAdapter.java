package com.sparshik.yogicapple.ui.groups;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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


/**
 * Populates list of joined groups for given user
 */
public class GroupAdapter extends FirebaseRecyclerAdapter<SupportGroup, GroupViewHolder> {
    private Activity mActivity;
    private String mEncodedEmail;

    public GroupAdapter(Activity activity, Class<SupportGroup> modelClass, int modelLayout, Class<GroupViewHolder> viewHolderClass, Query ref, String encodedEmail) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mActivity = activity;
        this.mEncodedEmail = encodedEmail;
    }

    @Override
    protected void populateViewHolder(final GroupViewHolder viewHolder, SupportGroup supportGroup, int position) {
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
                Toast.makeText(mActivity, "Container", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.mGroupDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mActivity, "Delete", Toast.LENGTH_SHORT).show();

            }
        });
    }

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
