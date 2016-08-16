package com.sparshik.yogicapple.ui.packs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.sparshik.yogicapple.utils.Constants;

/**
 * Fragment to populate Program Packs List
 */
public class PacksListFragment extends Fragment {

    private final static String LOG_TAG = PacksListFragment.class.getSimpleName();
    private TextView mTextViewHeaderTitle, mTextViewHeaderBody;
    private ImageView mImageViewHeaderIcon, mImageViewHeaderPackImage;
    private LinearLayout mTopContainer;
    private String mProgramId;
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
                Program program = dataSnapshot.getValue(Program.class);
                if (program != null) {
                    mTextViewHeaderTitle.setText(program.getProgramTitle());
                    mTextViewHeaderBody.setText(program.getProgramDesc());
                    int darColor = ColorUtils.darkenColor(program.getProgramColorInt());
                    mTopContainer.setBackgroundColor(darColor);

                    /** Create Firebase Ref **/
                    DatabaseReference packsListRef = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl(Constants.FIREBASE_URL_PROGRAM_PACKS).child(mProgramId);

                    mPacksRecylerAdapter = new PacksRecylerAdapter(getActivity(), Pack.class,
                            R.layout.list_single_item_pack, PacksViewHolder.class, packsListRef, program.getProgramColorInt());

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
        mImageViewHeaderPackImage = (ImageView) view.findViewById(R.id.image_view_pack_image_header);
        mTopContainer = (LinearLayout) view.findViewById(R.id.header_container);
        mRecycleView = (RecyclerView) view.findViewById(R.id.recycle_view_pack);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View footer = getActivity().getLayoutInflater().inflate(R.layout.footer_empty, null);
    }
}
