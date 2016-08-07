package com.xda.one.util;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import com.xda.one.R;

import android.app.Application;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class OneApplication extends Application {

    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-12268453-6";

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    private final Map<TrackerName, Tracker> mTrackers = new HashMap<>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (mTrackers.containsKey(trackerId)) {
            return mTrackers.get(trackerId);
        }

        final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

        final Tracker tracker;
        switch (trackerId) {
            case APP_TRACKER:
                tracker = analytics.newTracker(PROPERTY_ID);
                break;
            case GLOBAL_TRACKER:
                tracker = analytics.newTracker(R.xml.global_tracker);
                break;
            default:
                tracker = analytics.newTracker(R.xml.global_tracker);
                break;
        }
        tracker.enableAdvertisingIdCollection(true);
        mTrackers.put(trackerId, tracker);

        return tracker;
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);

        // MultiDex.install(this);
    }
}