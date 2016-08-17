package com.sparshik.yogicapple.ui.packs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparshik.yogicapple.R;

/**
 * View Holder for ProgramPacks Recycle View
 */
public class PacksViewHolder extends RecyclerView.ViewHolder {
    ImageView mIconImage;
    TextView mTitleText, mShortDescText;
    FrameLayout mContainer;
    Button mStateButton;

    public PacksViewHolder(View itemView) {
        super(itemView);
        mIconImage = (ImageView) itemView.findViewById(R.id.image_view_icon_list);
        mTitleText = (TextView) itemView.findViewById(R.id.text_view_title_list);
        mShortDescText = (TextView) itemView.findViewById(R.id.text_view_short_desc_list);
        mContainer = (FrameLayout) itemView.findViewById(R.id.container_list_item);
        mStateButton = (Button) itemView.findViewById(R.id.button_state_list);
    }

}
