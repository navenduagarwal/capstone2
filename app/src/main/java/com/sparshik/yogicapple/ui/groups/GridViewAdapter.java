package com.sparshik.yogicapple.ui.groups;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.ChatImageOption;

import java.util.ArrayList;

/**
 * Adapter to populate profile image selection grid
 */
public class GridViewAdapter extends ArrayAdapter<ChatImageOption> {
    private Activity mActivity;
    private int mLayoutResourceid;
    private ArrayList<ChatImageOption> mList = new ArrayList();

    public GridViewAdapter(Activity activity, int layoutResourceId, ArrayList<ChatImageOption> list) {
        super(activity, layoutResourceId, list);
        this.mActivity = activity;
        this.mLayoutResourceid = layoutResourceId;
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater layoutInflater = mActivity.getLayoutInflater();
            row = layoutInflater.inflate(mLayoutResourceid, parent, false);
            holder = new ViewHolder();
            holder.mProfileImageOption = (ImageView) row.findViewById(R.id.chat_profile_image_option);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        ChatImageOption imageItem = mList.get(position);

        holder.mProfileImageOption.setImageResource(imageItem.getChatImageResId());
        return row;
    }

    public static class ViewHolder {
        ImageView mProfileImageOption;
    }
}
