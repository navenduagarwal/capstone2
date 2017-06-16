package com.sparshik.yogicapple.ui.programs;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.Program;

/**
 * Firebase Adapter for populating programs list
 */
public class ProgramsListAdapter extends FirebaseListAdapter<Program> {

    public ProgramsListAdapter(Activity activity, Class<Program> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View v, Program model, int position) {

        final ImageView iconImage = (ImageView) v.findViewById(R.id.image_view_icon_list);
        TextView titleText = (TextView) v.findViewById(R.id.text_view_title_list);
        TextView builByText = (TextView) v.findViewById(R.id.text_view_built_by_partner);


        if (!TextUtils.isEmpty(model.getProgramIconUrl())) {
            Glide.with(mActivity).load(model.getProgramIconUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(iconImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mActivity.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    iconImage.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            iconImage.setVisibility(View.INVISIBLE);
        }

        titleText.setText(model.getProgramTitle());
        if (!TextUtils.isEmpty(model.getProgramBuiltBy())) {
            builByText.setText(v.getResources().getString(R.string.format_built_by, model.getProgramBuiltBy()));
        } else {
            builByText.setVisibility(View.GONE);
        }
    }
}
