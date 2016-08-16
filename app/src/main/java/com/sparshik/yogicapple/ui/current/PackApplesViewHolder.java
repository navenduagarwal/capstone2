package com.sparshik.yogicapple.ui.current;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.utils.ColorUtils;
import com.sparshik.yogicapple.views.CircleProgressBar;

/**
 * View Holder for apples
 */
public class PackApplesViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public PackApplesViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setBackGroundColor(int color) {
        RelativeLayout field = (RelativeLayout) mView.findViewById(R.id.container_list_item);
        field.setBackgroundColor(color);
    }

    public void setSeqText(String seq) {
        TextView field = (TextView) mView.findViewById(R.id.apple_number);
        field.setText(seq);
    }

    public void setProgress(int progress) {
        CircleProgressBar field = (CircleProgressBar) mView.findViewById(R.id.apple_progress_circle);
        field.setProgress(progress);
    }

    public void setCircleColor(int color) {
        int altColor = ColorUtils.darkenColor(color);
        CircleProgressBar field = (CircleProgressBar) mView.findViewById(R.id.apple_progress_circle);
        field.setBackgroundColor(color);
        field.setInnerColor(altColor);
        field.setStrokeColor(altColor);
        field.setUseRing(true);
    }

    public void setLineColor(int color) {
        View field = mView.findViewById(R.id.current_line);
        field.setBackgroundColor(color);
    }

    public void setLockColor(int color) {
        ImageView field = (ImageView) mView.findViewById(R.id.apple_locked);
        field.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public void setDownloadTextColor(int color) {
        TextView field = (TextView) mView.findViewById(R.id.download_text);
        field.setBackgroundColor(color);
    }

}