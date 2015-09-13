package com.groooo.android.popularmovie;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.ProviderTestCase2;

import com.groooo.android.popularmovie.data.MovieContract;
import com.groooo.android.popularmovie.data.MovieDbOpenHelper;
import com.groooo.android.popularmovie.data.MovieProvider;


public class MovieProviderTest extends ProviderTestCase2<MovieProvider> {

    public MovieProviderTest() {
        super(MovieProvider.class, MovieContract.CONTENT_AUTHORITY);
    }

    public void testBasicCRUD() {
        Uri referenceUri = MovieContract.MovieEntry.CONTENT_URI;
        MovieProvider provider = getProvider();

        ContentValues insertValues = TestUtilities.createSampleMovie();

        // Test on if data is inserted.
        Uri insertUri = provider.insert(referenceUri, insertValues);
        long movieId = MovieContract.MovieEntry.getMoviesIdFromUri(insertUri);
        assertTrue("It should be able to insert data.", movieId != -1);

        // Test on if data is existed in table.
        Cursor cursor = provider.query(insertUri, null, null, null, null);
        assertTrue("It should be returning data we have just inserted.", cursor.moveToFirst());

        // Validate data after insert.
        TestUtilities.validateCurrentRecord("It should be passed validation on insert.", cursor, insertValues);
        assertFalse("It should return only one row.", cursor.moveToNext());

        ContentValues updateValues = new ContentValues(insertValues);
        updateValues.put(MovieContract.MovieEntry.COLUMN_ITEM_ID, movieId);
        updateValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Hey, I'm changed");
        int count = provider.update(insertUri, updateValues,
                MovieContract.MovieEntry._ID + "=?",
                new String[] { Long.toString(movieId) });

        assertEquals("It should be affected to one row.", count, 1);

        cursor = provider.query(insertUri, null, null, null, null);
        assertTrue("It should be returning data we have just updated.", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("It should be passed validation on update.", cursor, updateValues);

        assertFalse("It should return only one row that updated.", cursor.moveToNext());
    }

    public void testBulkInsert() {
        ContentValues[] testValues = TestUtilities.createSampleMoviesArray();
        int count = getProvider().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, testValues);
        assertEquals("In should insert 2 rows.", count, 2);

        Cursor cursor = getProvider().query(MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, null);
        assertTrue(cursor.moveToFirst());
        assertTrue(cursor.moveToNext());
        assertFalse(cursor.moveToNext());
    }

    public void testGetType() {
        String typeStr = getProvider().getType(MovieContract.MovieEntry.CONTENT_URI);
        assertEquals("It should return MovieContract.MovieEntry.CONTENT_TYPE", typeStr, MovieContract.MovieEntry.CONTENT_TYPE);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    // A helper method to clean up table before test.
    private void deleteAllRecords() {
        SQLiteDatabase db = new MovieDbOpenHelper(mContext).getWritableDatabase();
        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.close();
    }

    // A helper method to insert a sample row into table.
    private void insertMovieRow() {
        SQLiteDatabase db = new MovieDbOpenHelper(mContext).getWritableDatabase();
        db.insert(MovieContract.MovieEntry.TABLE_NAME, null, TestUtilities.createSampleMovie());
        db.close();
    }
}
