package com.groooo.android.popularmovie.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.groooo.android.popularmovie.R;

import java.text.SimpleDateFormat;

public class Utility {
    public static final String LOG_TAG = Utility.class.getSimpleName();

    public static final String toBasicDateFormat(String format, long dateInMilliseconds) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat(format);
        return shortenedDateFormat.format(dateInMilliseconds);
    }

    public static String getSharedPreferenceSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_sort_by_default));
    }

    public static String textAsSmoothAsSilk(String text) {
        return (text == null || text.isEmpty() || text.equalsIgnoreCase("null"))? "-" : text;
    }
}
