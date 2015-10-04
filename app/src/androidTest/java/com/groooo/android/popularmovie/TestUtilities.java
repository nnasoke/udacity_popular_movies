package com.groooo.android.popularmovie;

import android.content.ContentValues;
import android.database.Cursor;

import com.groooo.android.popularmovie.helper.MdbContract;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class TestUtilities {

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public static ContentValues createSampleMovie() {
        ContentValues values = new ContentValues();
        values.put(MdbContract.MovieEntry.COLUMN_ITEM_ID, "76431");
        values.put(MdbContract.MovieEntry.COLUMN_TITLE, "Mad Max: Fury Road");
        values.put(MdbContract.MovieEntry.COLUMN_OVERVIEW, "An apocalyptic story set in the furthest reaches of our planet, in a stark desert landscape where humanity is broken, and most everyone is crazed fighting for the necessities of life. Within this world exist two rebels on the run who just might be able to restore order. There's Max, a man of action and a man of few words, who seeks peace of mind following the loss of his wife and child in the aftermath of the chaos. And Furiosa, a woman of action and a woman who believes her path to survival may be achieved if she can make it across the desert back to her childhood homeland.");
        values.put(MdbContract.MovieEntry.COLUMN_POSTER_PATH, "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg");
        values.put(MdbContract.MovieEntry.COLUMN_RELEASE_DATE, Calendar.getInstance().getTimeInMillis());
        values.put(MdbContract.MovieEntry.COLUMN_POPULARITY, 46.2);
        values.put(MdbContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7.7);
        return values;
    }

    public static ContentValues[] createSampleMoviesArray() {
        ContentValues[] values = new ContentValues[2];
        values[0] = new ContentValues();
        values[0].put(MdbContract.MovieEntry.COLUMN_ITEM_ID, "76431");
        values[0].put(MdbContract.MovieEntry.COLUMN_TITLE, "Mad Max: Fury Road");
        values[0].put(MdbContract.MovieEntry.COLUMN_OVERVIEW, "An apocalyptic story set in the furthest reaches of our planet, in a stark desert landscape where humanity is broken, and most everyone is crazed fighting for the necessities of life. Within this world exist two rebels on the run who just might be able to restore order. There's Max, a man of action and a man of few words, who seeks peace of mind following the loss of his wife and child in the aftermath of the chaos. And Furiosa, a woman of action and a woman who believes her path to survival may be achieved if she can make it across the desert back to her childhood homeland.");
        values[0].put(MdbContract.MovieEntry.COLUMN_POSTER_PATH, "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg");
        values[0].put(MdbContract.MovieEntry.COLUMN_RELEASE_DATE, Calendar.getInstance().getTimeInMillis());
        values[0].put(MdbContract.MovieEntry.COLUMN_POPULARITY, 46.2);
        values[0].put(MdbContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7.7);
        values[1] = new ContentValues();
        values[1].put(MdbContract.MovieEntry.COLUMN_ITEM_ID, "76432");
        values[1].put(MdbContract.MovieEntry.COLUMN_TITLE, "Iron Man");
        values[1].put(MdbContract.MovieEntry.COLUMN_OVERVIEW, "Sample overview");
        values[1].put(MdbContract.MovieEntry.COLUMN_POSTER_PATH, "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg");
        values[1].put(MdbContract.MovieEntry.COLUMN_RELEASE_DATE, Calendar.getInstance().getTimeInMillis());
        values[1].put(MdbContract.MovieEntry.COLUMN_POPULARITY, 40.2);
        values[1].put(MdbContract.MovieEntry.COLUMN_VOTE_AVERAGE, 5.2);
        return values;
    }
}
