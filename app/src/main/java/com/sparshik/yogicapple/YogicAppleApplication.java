package com.sparshik.yogicapple;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.FirebaseDatabase;

import timber.log.Timber;

/**
 * Includes one-time initialization of Firebase related code
 */
public class YogicAppleApplication extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//            FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        } else {
            Timber.plant(new CrashReportingTree());
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            if (t != null) {
                if (priority == Log.ERROR) {
                    FirebaseCrash.logcat(Log.ERROR, tag, message);
                } else if (priority == Log.WARN) {
                    FirebaseCrash.logcat(Log.WARN, tag, message);
                }
            }
        }
    }

}
