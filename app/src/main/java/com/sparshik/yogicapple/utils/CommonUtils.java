package com.sparshik.yogicapple.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * Common methods
 */

public class CommonUtils {

    public static void openWebsite(Activity activity, String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        activity.startActivity(intent);
    }
}
