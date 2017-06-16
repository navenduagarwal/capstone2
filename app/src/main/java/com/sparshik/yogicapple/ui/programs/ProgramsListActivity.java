package com.sparshik.yogicapple.ui.programs;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.ui.BaseActivity;

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
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ProgramsListFragment())
                    .commit();
        }
    }
}
