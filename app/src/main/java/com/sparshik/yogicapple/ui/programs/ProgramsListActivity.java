package com.sparshik.yogicapple.ui.programs;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.ui.BaseActivity;
import com.sparshik.yogicapple.utils.Constants;

/**
 * Handles list generation of programs
 */
public class ProgramsListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

                /* Add back button to the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_ENCODED_EMAIL, mEncodedEmail);
            ProgramsListFragment programsListFragment = new ProgramsListFragment();
            programsListFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, programsListFragment)
                    .commit();
        }
    }
}
