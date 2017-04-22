package com.polydelic.oliverdixon.ollysenhancedlist.src;

import android.util.Log;

public class Constants {
    private static final String APP_NAME = "polydelic-enhanced-list";

    public static void LogError(final String error) {
        Log.e(APP_NAME, error);
    }

    public static void printStackTrace() {
        Constants.LogError(Log.getStackTraceString(new Exception()));
    }
}
