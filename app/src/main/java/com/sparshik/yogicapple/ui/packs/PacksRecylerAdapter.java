package com.sparshik.yogicapple.ui.packs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.Pack;

/**
 * Program Packs List Adapter
 */
public class PacksRecylerAdapter extends FirebaseRecyclerAdapter<Pack, PacksViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context mContext;
    private int mProgramColor;

    public PacksRecylerAdapter(Context context, Class<Pack> modelClass, int modelLayout, Class<PacksViewHolder> viewHolderClass, Query ref, int programColor) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mContext = context;
        this.mProgramColor = programColor;
    }

    @Override
    protected void populateViewHolder(PacksViewHolder viewHolder, Pack model, int position) {

        if (model.getPackIconUrl() != null) {
            viewHolder.setIconImage(mContext, model.getPackIconUrl());
        } else {
            viewHolder.setIconImageVisiblity(View.INVISIBLE);
        }

        viewHolder.setTitleText(model.getPackTitle());

        viewHolder.setBackGroundColor(mProgramColor);

        if (model.getPackShortDesc() != null) {
            viewHolder.setShortDescText(model.getPackShortDesc());
        } else {
            viewHolder.setShortDescVisibility(View.INVISIBLE);
        }
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), PackApplesListActivity.class);
//                /**
//                 *Get the program ID using the adapter's get ref method to get the Firebase
//                 * ref and then grab the key.
//                 */
//                String packId = mProgramPacksListAdapter.getRef(position).getKey();
//                intent.putExtra(Constants.KEY_PACK_ID, packId);
//                intent.putExtra(Constants.KEY_PROGRAM_ID, mProgramId);
//                    /* Start an activity showing the packs for selected program */
//                startActivity(intent);
            }
        });
    }

    @Override
    public PacksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_empty, parent, false);
            return new FooterViewHolder(v);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    class FooterViewHolder extends PacksViewHolder {
        TextView txtTitleFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            this.txtTitleFooter = (TextView) itemView.findViewById(R.id.text_view_listview_footer);
        }
    }
}
