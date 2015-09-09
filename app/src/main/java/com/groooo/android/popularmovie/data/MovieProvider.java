package com.groooo.android.popularmovie.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDbOpenHelper mOpenDbHelper;

    private static final int MOVIES = 99;
    private static final int MOVIES_MOST_POPULAR = 100;
    private static final int MOVIES_HIGHEST_RATE = 200;
    private static final int MOVIE_ITEM = 300;

    @Override
    public boolean onCreate() {
        mOpenDbHelper = new MovieDbOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Cursor cursor = null;

        switch (match) {
            case MOVIES_MOST_POPULAR:
                cursor = queryMostPopularMovies(projection, selection, selectionArgs);
                break;
            case MOVIES_HIGHEST_RATE:
                cursor = queryHighestRateMovies(projection, selection, selectionArgs);
                break;
            case MOVIE_ITEM:
                cursor = getSingleMovie(uri, projection);
                break;
            default:
                throw new UnsupportedOperationException("Can not find Uri " + uri.toString());
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        String type = null;
        if (match == MOVIES)
            type = MovieContract.MovieEntry.CONTENT_TYPE;
        else
            throw new UnsupportedOperationException("Can not find Uri " + uri.toString());
        return type;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        if (match == MOVIES) {
            final SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
            final long rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
            db.close();

            if (rowId != -1) {
                long itemId = contentValues.getAsLong(MovieContract.MovieEntry.COLUMN_ITEM_ID);
                returnUri = MovieContract.MovieEntry.buildMoviesUri(itemId);
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
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ITEM:
                long movieId = MovieContract.MovieEntry.getMoviesIdFromUri(uri);
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_ITEM_ID + " = ?",
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
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, contents, selection, selectionArgs);
                break;
            case MOVIE_ITEM: // Update one row that meet movie_id.
                long movieId = MovieContract.MovieEntry.getMoviesIdFromUri(uri);
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contents,
                        MovieContract.MovieEntry.COLUMN_ITEM_ID + " = ?",
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
                    if (db.insert(MovieContract.MovieEntry.TABLE_NAME, null, content) != -1) {
                        insertCount++;
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error on bulkInsert", e);
            }
            finally {
                db.endTransaction();
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
        final String pathSortByMostPopular = String.format("%s/%s",
                MovieContract.PATH_MOST_POPULAR, MovieContract.PATH_MOVIES);
        final String pathSortByHighestRate = String.format("%s/%s",
                MovieContract.PATH_HIGHEST_RATE, MovieContract.PATH_MOVIES);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MovieProvider.MOVIES);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, pathSortByHighestRate, MovieProvider.MOVIES_HIGHEST_RATE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, pathSortByMostPopular, MovieProvider.MOVIES_MOST_POPULAR);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", MovieProvider.MOVIE_ITEM);
        return matcher;
    }

    private Cursor getSingleMovie(Uri uri, String[] projection) {
        final String test = MovieContract.MovieEntry.COLUMN_ITEM_ID + "=?";
        final String[] testValues = new String[] {
                Long.toString(MovieContract.MovieEntry.getMoviesIdFromUri(uri))
        };
        return mOpenDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection, test,
                testValues, null, null, null);
    }

    private Cursor queryHighestRateMovies(String[] projection, String selection, String[] selectionArgs) {
        final String sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        return mOpenDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor queryMostPopularMovies(String[] projection, String selection, String[] selectionArgs) {
        final String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        return mOpenDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }
}
