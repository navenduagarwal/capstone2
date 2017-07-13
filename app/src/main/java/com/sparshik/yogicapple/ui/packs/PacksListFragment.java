package com.sparshik.yogicapple.ui.packs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.Pack;
import com.sparshik.yogicapple.model.Program;
import com.sparshik.yogicapple.ui.programs.ProgramsListActivity;
import com.sparshik.yogicapple.utils.ColorUtils;
import com.sparshik.yogicapple.utils.CommonUtils;
import com.sparshik.yogicapple.utils.Constants;
import com.sparshik.yogicapple.utils.RecyclerViewUtils;

/**
 * Fragment to populate Program Packs List
 */
public class PacksListFragment extends Fragment {

    private final static String LOG_TAG = PacksListFragment.class.getSimpleName();
    private TextView mTextViewHeaderTitle, mTextViewHeaderBody, mTextViewPackBuiltBy;
    private Button mButtonReadMore;
    private ImageView mImageViewHeaderIcon, mImageViewHeaderPackImage;
    private LinearLayout mTopContainer;
    private String mProgramId, mCurrentProgramId, mCurrentPackId;
    private RecyclerView mRecycleView;
    private PacksRecylerAdapter mPacksRecylerAdapter;

    public PacksListFragment() {
        super();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_packs, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mCurrentProgramId = preferences.getString(Constants.KEY_CURRENT_PROGRAM_ID, null);
        mCurrentPackId = preferences.getString(Constants.KEY_CURRENT_PACK_ID, null);

        Intent intent = getActivity().getIntent();
        mProgramId = intent.getStringExtra(Constants.KEY_PROGRAM_ID);
        if (mProgramId == null) {
            /* No point in continuing without a valid ID. */
            Log.e(getActivity().getClass().getSimpleName(), "Program Id is null");
            startActivity(new Intent(getActivity(), ProgramsListActivity.class));
        }
        initializeScreen(rootView);

        DatabaseReference programRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_PROGRAMS).child(mProgramId);

        programRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Program program = dataSnapshot.getValue(Program.class);
                if (program != null) {
                    mTextViewHeaderTitle.setText(program.getProgramTitle());
                    mTextViewHeaderBody.setText(program.getProgramDesc());
                    int lightColor = ColorUtils.darkenColorLess(program.getProgramColorInt());
                    int darkColor = ColorUtils.darkenColor(program.getProgramColorInt());
                    mTopContainer.setBackgroundColor(lightColor);
                    if (program.getProgramBuiltBy() != null) {
                        mTextViewPackBuiltBy.setText(getResources().getString(R.string.format_built_by, program.getProgramBuiltBy()));
                    }
                    if (program.getProgramDevSiteUrl() != null) {
                        mButtonReadMore.setVisibility(View.VISIBLE);
                        mButtonReadMore.setText(R.string.read_more);
                        mButtonReadMore.setBackgroundColor(darkColor);
                        mButtonReadMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonUtils.openWebsite(getActivity(), program.getProgramDevSiteUrl());
                            }
                        });
                    } else {
                        mButtonReadMore.setVisibility(View.GONE);

                    }

                    if (!TextUtils.isEmpty(program.getProgramIconUrl())) {
                        Glide.with(getActivity()).load(program.getProgramIconUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(mImageViewHeaderIcon) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                mImageViewHeaderIcon.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                    } else {
                        mImageViewHeaderIcon.setVisibility(View.INVISIBLE);
                    }


                    /** Create Firebase Ref **/
                    DatabaseReference packsListRef = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl(Constants.FIREBASE_URL_PROGRAM_PACKS).child(mProgramId);

                    mPacksRecylerAdapter = new PacksRecylerAdapter(getActivity(), Pack.class,
                            R.layout.list_single_item_pack, PacksViewHolder.class, packsListRef, program.getProgramColorInt(), mProgramId, mCurrentPackId, mCurrentProgramId);

                    mRecycleView.setAdapter(mPacksRecylerAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, getActivity().getResources().getString(R.string.log_error_the_read_failed));
            }
        });


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPacksRecylerAdapter.cleanup();
    }

    public void initializeScreen(View view) {
        mTextViewHeaderTitle = (TextView) view.findViewById(R.id.text_view_title_header);
        mTextViewHeaderBody = (TextView) view.findViewById(R.id.text_view_body_header);
        mImageViewHeaderIcon = (ImageView) view.findViewById(R.id.image_view_icon_header);
        mTextViewPackBuiltBy = (TextView) view.findViewById(R.id.text_view_built_by_partner);
        mButtonReadMore = (Button) view.findViewById(R.id.button_read_more);
        mImageViewHeaderPackImage = (ImageView) view.findViewById(R.id.image_view_pack_image_header);
        mTopContainer = (LinearLayout) view.findViewById(R.id.header_container);
        mRecycleView = (RecyclerView) view.findViewById(R.id.recycle_view_pack);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.addItemDecoration(new RecyclerViewUtils.DividerItemDecoration(getResources().getDrawable(R.drawable.recycler_divider)));
        View footer = getActivity().getLayoutInflater().inflate(R.layout.footer_empty, null);
    }
}
