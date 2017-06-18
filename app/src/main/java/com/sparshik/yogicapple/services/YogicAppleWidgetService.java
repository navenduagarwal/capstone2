package com.sparshik.yogicapple.services;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.sparshik.yogicapple.ui.widget.YogicAppleWidgetDataProvider;

/**
 * Service for Widget
 */

public class YogicAppleWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new YogicAppleWidgetDataProvider(this, intent);
    }
}