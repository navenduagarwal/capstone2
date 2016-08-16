package com.sparshik.yogicapple.ui.packs;

import android.app.Activity;
import android.view.View;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;
import com.sparshik.yogicapple.model.Pack;

/**
 * Program Packs List Adapter
 */
public class ProgramPacksListAdapter extends FirebaseListAdapter<Pack> {

    public ProgramPacksListAdapter(Activity activity, Class<Pack> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
    }

    @Override
    protected void populateView(View v, Pack model, int position) {

    }
}
