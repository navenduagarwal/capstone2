package com.sparshik.yogicapple.ui.packs;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.sparshik.yogicapple.R;

/**
 * View Holder for ProgramPacks Recycle View
 */
public class PacksViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public PacksViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setIconImage(final Context context, String url) {
        final ImageView field = (ImageView) mView.findViewById(R.id.image_view_icon_list);
        Glide.with(context).load(url).asBitmap().centerCrop().into(new BitmapImageViewTarget(field) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                field.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public void setIconImageVisiblity(int visibility) {
        ImageView field = (ImageView) mView.findViewById(R.id.image_view_icon_list);
        field.setVisibility(visibility);
    }

    public void setTitleText(String title) {
        TextView field = (TextView) mView.findViewById(R.id.text_view_title_list);
        field.setText(title);
    }

    public void setShortDescText(String desc) {
        TextView field = (TextView) mView.findViewById(R.id.text_view_short_desc_list);
        field.setText(desc);
    }

    public void setShortDescVisibility(int visibility) {
        TextView field = (TextView) mView.findViewById(R.id.text_view_short_desc_list);
        field.setVisibility(visibility);
    }

    public void setBackGroundColor(int color) {
        FrameLayout field = (FrameLayout) mView.findViewById(R.id.container_list_item);
        field.setBackgroundColor(color);
    }

    public void setButtonState(int visibility) {
        Button field = (Button) mView.findViewById(R.id.button_state_list);
        field.setVisibility(visibility);
    }
}
