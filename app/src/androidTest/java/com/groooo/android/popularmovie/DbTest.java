package com.groooo.android.popularmovie;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.groooo.android.popularmovie.data.MovieContract;
import com.groooo.android.popularmovie.data.MovieDbOpenHelper;

import java.util.HashSet;

public class DbTest extends AndroidTestCase {

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableHashSet = new HashSet<>();
        tableHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbOpenHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbOpenHelper(mContext).getWritableDatabase();
        // test that if table has been created
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());
        do {
            tableHashSet.remove(c.getString(0));
        } while( c.moveToNext() );
        assertTrue("Error: Your database was created without movies table", tableHashSet.isEmpty());

        // test that if columns have been created correctly
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.", c.moveToFirst());

        final HashSet<String> columnHashSet = new HashSet<>();
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        int columnIndex = c.getColumnIndex("name");
        do {
            columnHashSet.remove(c.getString(columnIndex));
        } while (c.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required columns", columnHashSet.isEmpty());

        c.close();
        db.close();
    }

    public void testMoviesTable() throws  Throwable {
        final ContentValues testMovieValues = TestUtilities.createSampleMovie();
        SQLiteDatabase db = new MovieDbOpenHelper(mContext).getWritableDatabase();

        // test that data can be save.
        long rowNum;
        rowNum = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testMovieValues);
        assertTrue("Error: Unable to save movies data", rowNum != -1);

        // test that data can be retrieved
        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: No record returned", cursor.moveToFirst());

        // validate cursor's result with the original testing data.
        TestUtilities.validateCurrentRecord("Error: Data that saved previously is not corrected", cursor, testMovieValues);

        // test that there should only one record will be saved
        assertFalse("It should be only one record be returned", cursor.moveToNext());

        cursor.close();
        db.close();
    }
}
