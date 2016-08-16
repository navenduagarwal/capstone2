package com.sparshik.yogicapple.ui.programs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.model.Program;
import com.sparshik.yogicapple.ui.packs.PacksListActivity;
import com.sparshik.yogicapple.utils.Constants;

/**
 * Fragment for Programs
 */
public class ProgramsListFragment extends Fragment {
    private ListView mListView;
    private ProgramsListAdapter mProgramListAdapter;
    private String mEncodedEmail;

    public ProgramsListFragment() {
        super();
        /* Required empty public constructor */
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEncodedEmail = this.getArguments().getString(Constants.KEY_ENCODED_EMAIL);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View rootView = inflater.inflate(R.layout.fragment_programs_list, container, false);
        Log.d("Testing", "Hello");

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen(rootView);

        /** Create Firebase Ref **/
        DatabaseReference programsListRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.FIREBASE_URL_PROGRAMS);
        mProgramListAdapter = new ProgramsListAdapter(getActivity(), Program.class,
                R.layout.list_single_item_program, programsListRef);
        mListView.setAdapter(mProgramListAdapter);

        /**
         * Set interactive bits, such as click events/adapters
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Program program = mProgramListAdapter.getItem(position);
                if (program != null) {
                    Intent intent = new Intent(getActivity(), PacksListActivity.class);
                    /**
                     *Get the program ID using the adapter's get ref method to get the Firebase
                     * ref and then grab the key.
                     */
                    String programId = mProgramListAdapter.getRef(position).getKey();
                    intent.putExtra(Constants.KEY_PROGRAM_ID, programId);
                    /* Start an activity showing the packs for selected program */
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mProgramListAdapter.cleanup();
    }

    private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_programs_list);
        View footer = getActivity().getLayoutInflater().inflate(R.layout.footer_empty, null);
        mListView.addFooterView(footer);
    }
}