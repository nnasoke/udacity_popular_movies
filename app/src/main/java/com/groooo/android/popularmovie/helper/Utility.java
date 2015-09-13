package com.groooo.android.popularmovie.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.groooo.android.popularmovie.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utility {
    public static final String LOG_TAG = Utility.class.getSimpleName();

    // Convert timestamp to formatted date string.
    public static final String timestampToBasicFormat(String format, long dateInMilliseconds) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat(format);
        return shortenedDateFormat.format(dateInMilliseconds);
    }

    // Convert date to timestamp.
    public static long dateToMilliseconds(String dateFormat, String date) {
        long milliSec = -1;
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            milliSec = format.parse(date).getTime();
        } catch (ParseException e) {
            Log.e(LOG_TAG, String.format("Error parsing date %s from format %s", date, dateFormat));
        }
        return milliSec;
    }

    // Read sort order valur from shared preference.
    public static String getSharedPreferenceSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_sort_by_default));
    }

    // To be displaying text as dash(-) where null or empty string presents.
    public static String textToSmoothDisplay(String text) {
        return (text == null || text.isEmpty() || text.equalsIgnoreCase("null"))? "-" : text;
    }

}
