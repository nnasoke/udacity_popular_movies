package com.groooo.android.popularmovie.helper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MdbContentProvider extends ContentProvider {

    private static final String LOG_TAG = MdbContentProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MdbSQLiteHelper mOpenDbHelper;

    private static final int MOVIES = 100;
    private static final int MOVIE_ITEM = 200;

    @Override
    public boolean onCreate() {
        mOpenDbHelper = new MdbSQLiteHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Cursor cursor = null;
        switch (match) {
            case MOVIES:
                cursor = mOpenDbHelper.getReadableDatabase().query(MdbContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_ITEM:
                cursor = getSingleMovie(uri, projection);
        }
        if (cursor != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MdbContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ITEM:
                return MdbContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Can not find Uri " + uri.toString());
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        if (match == MOVIES) {
            final SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
            final long rowId = db.insert(MdbContract.MovieEntry.TABLE_NAME, null, contentValues);
            db.close();

            if (rowId != -1) {
                long itemId = contentValues.getAsLong(MdbContract.MovieEntry.COLUMN_ITEM_ID);
                returnUri = MdbContract.MovieEntry.buildMoviesUri(itemId);
            }
            else {
                throw new android.database.SQLException("Failed to insert row into " + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (returnUri != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
        int rowsDeleted = -1;

        switch (match) {
            case MOVIES:
                // All rows will be deleted if no condition specified.
                if (selection == null) selection = "1";
                rowsDeleted = db.delete(MdbContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ITEM:
                long movieId = MdbContract.MovieEntry.getMoviesIdFromUri(uri);
                rowsDeleted = db.delete(MdbContract.MovieEntry.TABLE_NAME,
                        MdbContract.MovieEntry.COLUMN_ITEM_ID + " = ?",
                        new String[] { Long.toString(movieId) });
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        db.close();

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contents, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
        int rowsUpdated = -1;

        switch (match) {
            case MOVIES: // Update all that meet criteria.
                rowsUpdated = db.update(MdbContract.MovieEntry.TABLE_NAME, contents, selection, selectionArgs);
                break;
            case MOVIE_ITEM: // Update one row that meet movie_id.
                long movieId = MdbContract.MovieEntry.getMoviesIdFromUri(uri);
                rowsUpdated = db.update(MdbContract.MovieEntry.TABLE_NAME,
                        contents,
                        MdbContract.MovieEntry.COLUMN_ITEM_ID + " = ?",
                        new String[] { Long.toString(movieId) });
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        db.close();

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] contents) {
        final int match = sUriMatcher.match(uri);
        int insertCount = 0;

        if (match == MOVIES) {
            final SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues content : contents) {
                    if (db.insert(MdbContract.MovieEntry.TABLE_NAME, null, content) != -1) {
                        insertCount++;
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error on bulkInsert", e);
            }
            finally {
                db.endTransaction();
                db.close();
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (insertCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return insertCount;
    }

    @Override
    public void shutdown() {
        mOpenDbHelper.close();
        super.shutdown();
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MdbContract.CONTENT_AUTHORITY, MdbContract.PATH_MOVIES, MdbContentProvider.MOVIES);
        matcher.addURI(MdbContract.CONTENT_AUTHORITY, MdbContract.PATH_MOVIES + "/#", MdbContentProvider.MOVIE_ITEM);
        return matcher;
    }

    private Cursor getSingleMovie(Uri uri, String[] projection) {
        final String test = MdbContract.MovieEntry.COLUMN_ITEM_ID + "=?";
        final String[] testValues = new String[] {
                Long.toString(MdbContract.MovieEntry.getMoviesIdFromUri(uri))
        };
        return mOpenDbHelper.getReadableDatabase().query(MdbContract.MovieEntry.TABLE_NAME,
                projection,
                test,
                testValues, null, null, null);
    }
}
