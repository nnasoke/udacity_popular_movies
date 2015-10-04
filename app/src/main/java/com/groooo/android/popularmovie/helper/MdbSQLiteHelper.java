package com.groooo.android.popularmovie.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MdbSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    public static final String DATABASE_NAME = "movies.db";

    public MdbSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + MdbContract.MovieEntry.TABLE_NAME + " (" +
                MdbContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MdbContract.MovieEntry.COLUMN_ITEM_ID + " TEXT NOT NULL, " +
                MdbContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MdbContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NULL, " +
                MdbContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NULL, " +
                MdbContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NULL, " +
                MdbContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MdbContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MdbContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                // To assure the application have just one weather entry per movie
                // it's created a UNIQUE constraint with REPLACE strategy
                "UNIQUE (" + MdbContract.MovieEntry.COLUMN_ITEM_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MdbContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
