/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_YEAR = "year";
    public static final String KEY_GENRE = "genre";
    public static final String KEY_SYNOPSIS = "synopsis";
    public static final String KEY_POSTER = "posterurl";
    public static final String KEY_TRAILER = "trailerurl";
    public static final String KEY_ROWID = "_id";
    
    private static final String DATABASE_NAME = "films.sqlite";
    private static final String DATABASE_TABLE_MOVIES = "movies";
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "MoviesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_MOVIES =
        "CREATE TABLE " + DATABASE_TABLE_MOVIES + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + KEY_TITLE + " TEXT NOT NULL, " + KEY_YEAR + " TEXT NOT NULL, "
        + KEY_GENRE + " TEXT, " + KEY_SYNOPSIS + " TEXT, " 
        + KEY_POSTER + " TEXT, " + KEY_TRAILER + " TEXT" + ");";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_MOVIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MOVIES);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public MovieDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the movies database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public MovieDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new movie using the title, genre and synopsis provided. If the movie is
     * successfully created return the new rowId for that movie, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the movie
     * @param genre the genre of the movie
     * @param synopsis the synopsis of the movie
     * @return rowId or -1 if failed
     */
    public long createMovie(String title, String year, String genre, String synopsis, String posterurl, String trailerurl) {
        ContentValues initialValues = new ContentValues();       
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_YEAR, year);
        initialValues.put(KEY_GENRE, genre);
        initialValues.put(KEY_SYNOPSIS, synopsis);
        initialValues.put(KEY_POSTER, posterurl);
        initialValues.put(KEY_TRAILER,trailerurl);

        return mDb.insert(DATABASE_TABLE_MOVIES, null, initialValues);
    }

    /**
     * Delete the movie with the given rowId
     * 
     * @param rowId id of movie to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteMovie(long rowId) {

        return mDb.delete(DATABASE_TABLE_MOVIES, KEY_ROWID + "=" + rowId, null) > 0;
    }


    /**
     * Return a Cursor over the list of all movies in the database
     * 
     * @return Cursor over all movies
     */
    public Cursor fetchAllMovies() {
    	
    	String orderBy;
    	if(globals.sortByTitle())
    		orderBy = KEY_TITLE;
    	else orderBy = KEY_YEAR;

    	return mDb.query(DATABASE_TABLE_MOVIES, new String[] {KEY_ROWID, KEY_TITLE, KEY_YEAR}, "genre LIKE " + "'%" + globals.getCurrentGenre() + "%'", null, null, null, orderBy);
    }

    /**
     * Return a Cursor positioned at the movie that matches the given rowId
     * 
     * @param rowId id of movie to retrieve
     * @return Cursor positioned to matching movie, if found
     * @throws SQLException if movie could not be found/retrieved
     */
    public Cursor fetchMovie(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE_MOVIES, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_YEAR, KEY_GENRE, KEY_SYNOPSIS, KEY_POSTER, KEY_TRAILER}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Checks if the movie already exists
     * 
     * @param movie ID
     * @return boolean
     */
    public boolean movieExists(String title) {

        Cursor mCursor =

            mDb.rawQuery("select _id FROM " + DATABASE_TABLE_MOVIES + " WHERE " + KEY_TITLE + "=\"" + title + "\"", null);
        boolean exists = (mCursor.getCount() > 0);
        mCursor.close();
        return exists;

    }
}
