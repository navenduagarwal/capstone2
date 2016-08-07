package com.sparshik.yogicapple.utils;

import android.graphics.Color;

/**
 * Color utilities
 */
public class ColorUtils {
    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.75f;
        return Color.HSVToColor(hsv);
    }

    public static int darkenColorLess(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.9f;
        return Color.HSVToColor(hsv);
    }
}
