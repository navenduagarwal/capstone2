package com.sparshik.yogicapple.ui.packs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
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
    protected void populateViewHolder(PacksViewHolder viewHolder, Pack model, int position) {

        if (model.getPackIconUrl() != null) {
            viewHolder.setIconImage(mContext, model.getPackIconUrl());
        } else {
            viewHolder.setIconImageVisiblity(View.INVISIBLE);
        }

        final String packId = this.getRef(position).getKey();
        if (packId.equals(mCurrentPackId)) {
            viewHolder.setButtonState(View.GONE);
        } else {
            viewHolder.setBackGroundColor(View.VISIBLE);
        }

        viewHolder.setTitleText(model.getPackTitle());

//        viewHolder.setBackGroundColor(mProgramColor);

        if (model.getPackShortDesc() != null) {
            viewHolder.setShortDescText(model.getPackShortDesc());
        } else {
            viewHolder.setShortDescVisibility(View.INVISIBLE);
        }
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!packId.equals(mCurrentPackId)) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    preferences.getString(Constants.KEY_ENCODED_EMAIL, null);
                    SharedPreferences.Editor spe = preferences.edit();
                    spe.putString(Constants.KEY_CURRENT_PACK_ID, packId).apply();
                    spe.putString(Constants.KEY_CURRENT_PROGRAM_ID, mProgramId).apply();
                    Toast.makeText(mContext, "Current Pack Changed Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });
    }

}
