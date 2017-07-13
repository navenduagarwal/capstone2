package com.sparshik.yogicapple.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sparshik.yogicapple.R;

/**
 * View holder for search group autocomplete
 */

public class AutoCompleteGroupViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextViewGroupName;

    public AutoCompleteGroupViewHolder(View itemView) {
        super(itemView);
        mTextViewGroupName = (TextView) itemView.findViewById(R.id.text_view_autocomplete_item);
    }
}
