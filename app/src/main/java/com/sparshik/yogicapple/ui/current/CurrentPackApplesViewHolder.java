package com.sparshik.yogicapple.ui.current;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.views.CircleProgressBar;

/**
 * View Holder for apples
 */
public class CurrentPackApplesViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout mContainer;
    TextView mSeqNumberText, mDownloadText;
    CircleProgressBar mProgressBar;
    View mLineView;
    ImageView mAppleState;


    public CurrentPackApplesViewHolder(View itemView) {
        super(itemView);
        mContainer = (RelativeLayout) itemView.findViewById(R.id.container_list_item);
        mSeqNumberText = (TextView) itemView.findViewById(R.id.apple_number);
        mProgressBar = (CircleProgressBar) itemView.findViewById(R.id.apple_progress_circle);
        mLineView = itemView.findViewById(R.id.current_line);
        mAppleState = (ImageView) itemView.findViewById(R.id.apple_locked);
        mDownloadText = (TextView) itemView.findViewById(R.id.download_text);

    }
}