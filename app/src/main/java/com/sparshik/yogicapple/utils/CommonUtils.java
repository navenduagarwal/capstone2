package com.sparshik.yogicapple.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Common methods
 */

public class CommonUtils {

    public static void openWebsite(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        context.startActivity(intent);
    }
}
