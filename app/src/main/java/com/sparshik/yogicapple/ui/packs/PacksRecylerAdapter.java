package com.sparshik.yogicapple.ui.packs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.Pack;
import com.sparshik.yogicapple.ui.main.MainActivity;
import com.sparshik.yogicapple.utils.Constants;

/**
 * Program Packs List Adapter
 */
public class PacksRecylerAdapter extends FirebaseRecyclerAdapter<Pack, PacksViewHolder> {
    private Context mContext;
    private int mProgramColor;
    private String mProgramId, mCurrentProgramId, mCurrentPackId;

    public PacksRecylerAdapter(Context context, Class<Pack> modelClass, int modelLayout, Class<PacksViewHolder> viewHolderClass,
                               Query ref, int programColor, String programId, String currentPackId, String currentProgramId) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mContext = context;
        this.mProgramColor = programColor;
        this.mProgramId = programId;
        this.mCurrentPackId = currentPackId;
        this.mCurrentProgramId = currentProgramId;
    }

    @Override
    protected void populateViewHolder(final PacksViewHolder viewHolder, Pack model, int position) {

        if (model.getPackIconUrl() != null) {
            viewHolder.mIconImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(model.getPackIconUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(viewHolder.mIconImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    viewHolder.mIconImage.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            viewHolder.mIconImage.setVisibility(View.INVISIBLE);
        }

        final String packId = this.getRef(position).getKey();

        if (packId.equals(mCurrentPackId)) {
            viewHolder.mStateButton.setVisibility(View.GONE);
        } else {
            viewHolder.mStateButton.setVisibility(View.VISIBLE);
        }

        viewHolder.mTitleText.setText(model.getPackTitle());

//        viewHolder.mContainer.setBackgroundColor(mProgramColor);

        if (model.getPackShortDesc() != null) {
            viewHolder.mShortDescText.setText(model.getPackShortDesc());
        } else {
            viewHolder.mShortDescText.setVisibility(View.INVISIBLE);
        }


        viewHolder.mStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!packId.equals(mCurrentPackId)) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    preferences.getString(Constants.KEY_ENCODED_EMAIL, null);
                    SharedPreferences.Editor spe = preferences.edit();
                    spe.putString(Constants.KEY_CURRENT_PACK_ID, packId).apply();
                    spe.putString(Constants.KEY_CURRENT_PROGRAM_ID, mProgramId).apply();
                    Toast.makeText(mContext, mContext.getString(R.string.pack_change), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });
    }

}
