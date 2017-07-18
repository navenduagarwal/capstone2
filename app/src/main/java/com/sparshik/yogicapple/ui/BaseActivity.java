package com.sparshik.yogicapple.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.newrelic.agent.android.NewRelic;
import com.sparshik.yogicapple.BuildConfig;
import com.sparshik.yogicapple.R;
import com.sparshik.yogicapple.ui.login.LoginActivity;
import com.sparshik.yogicapple.ui.signup.CreateAccountActivity;
import com.sparshik.yogicapple.utils.Constants;

import timber.log.Timber;

import static com.sparshik.yogicapple.utils.Constants.KEY_CONFIG_PACK_ID;
import static com.sparshik.yogicapple.utils.Constants.KEY_CONFIG_PROGRAM_ID;

/**
 * BaseActivity class is used as a base class for all activities in the app
 * It implements GoogleApiClient callbacks to enable "Logout" in all activities
 * and defines variables that are being shared across all activities
 */
public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    protected String mProvider, mEncodedEmail, mDefaultProgramId, mDefaultPackId;
    protected GoogleApiClient mGoogleApiClient;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseAuth mAuth;
    protected FirebaseRemoteConfig mFirebaseRemoteConfig;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NewRelic.withApplicationToken(getString(R.string.NEW_RELIC_API_KEY))
                .start(this.getApplication());


        /* Setup the Google API object to allow Google logins */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        /**
         * Build a GoogleApiClient with access to the Google Sign-In API and the
         * options specified by gso.
         */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        fetchRemoteValues();

        /**
         * Get provider and encoded email from SharedPreferences
         */
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        /* Get mEncodedEmail and mProvider from SharedPreferences, use null as default value */
        mEncodedEmail = sharedPreferences.getString(Constants.KEY_ENCODED_EMAIL, null);
        mProvider = sharedPreferences.getString(Constants.KEY_PROVIDER_ID, null);

        if (!((this instanceof LoginActivity) || (this instanceof CreateAccountActivity))) {
            mAuth = FirebaseAuth.getInstance();
            // [START auth_state_listener]
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        // User is signed out
                         /* Clear out shared preferences */
                        SharedPreferences.Editor spe = sharedPreferences.edit();
                        spe.putString(Constants.KEY_ENCODED_EMAIL, null);
                        spe.putString(Constants.KEY_PROVIDER_ID, null);
                        spe.apply();
                        takeUserToLoginScreenOnUnAuth();
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthListener);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!((this instanceof LoginActivity) || (this instanceof CreateAccountActivity))) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initializeBackground(LinearLayout linearLayout) {
        /**
         * Set different background image for landscape and portrait layouts
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linearLayout.setBackgroundColor(getResources().getColor(R.color.background));
        } else {
            linearLayout.setBackgroundColor(getResources().getColor(R.color.background));
        }
    }

    /**
     * Logs out the user from their current session and starts LoginActivity.
     * Also disconnects the mGoogleApiClient if connected and provider is Google
     */
    protected void logout() {
    /* logout if mProvider is not null */
        if (mProvider != null) {
            mAuth.signOut();
            if (mProvider.equals(Constants.GOOGLE_PROVIDER)) {
                /* Logout from Google+ */
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                //nothing
                            }
                        });
            }
        }
    }

    private void takeUserToLoginScreenOnUnAuth() {
        /* Move user to LoginActivity, and remove the backstack */
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void fetchRemoteValues() {
        long cacheExpiration = 3600; // 1 hour in seconds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.i("Fetch Succeeded");
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Timber.i("Fetch Failed");
                        }
                        mDefaultProgramId = mFirebaseRemoteConfig.getString(KEY_CONFIG_PROGRAM_ID);
                        mDefaultPackId = mFirebaseRemoteConfig.getString(KEY_CONFIG_PACK_ID);
                    }
                });
    }

}