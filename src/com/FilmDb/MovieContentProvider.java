package com.FilmDb;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MovieContentProvider extends ContentProvider {

	private static SQLiteDatabase      sqlDB;

	private DatabaseHelper      dbHelper;

	private static final String DATABASE_NAME    = "films.sqlite";

	private static final int    DATABASE_VERSION = 8;

	private static final String TABLE_NAME       = "movies";

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE_MOVIES =
		"CREATE TABLE " + TABLE_NAME + " (" + MovieDefinitions.MovieDefinition.KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ MovieDefinitions.MovieDefinition.KEY_MOVIEID + " INTEGER NOT NULL, " 
		+ MovieDefinitions.MovieDefinition.KEY_TITLE + " TEXT NOT NULL, " + MovieDefinitions.MovieDefinition.KEY_YEAR + " TEXT NOT NULL, "
		+ MovieDefinitions.MovieDefinition.KEY_DIRECTORS + " TEXT, " + MovieDefinitions.MovieDefinition.KEY_RUNTIME + " INTEGER, "
		+ MovieDefinitions.MovieDefinition.KEY_ACTORS + " TEXT, "
		+ MovieDefinitions.MovieDefinition.KEY_GENRE + " TEXT, " + MovieDefinitions.MovieDefinition.KEY_SYNOPSIS + " TEXT, " 
		+ MovieDefinitions.MovieDefinition.KEY_POSTER + " TEXT, " + MovieDefinitions.MovieDefinition.KEY_TRAILER + " TEXT, "
		+ MovieDefinitions.MovieDefinition.KEY_WATCHED + " BOOLEAN" + ");";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//create table to store user names
			db.execSQL(DATABASE_CREATE_MOVIES);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count=0;
		// get database to delete records
		sqlDB = dbHelper.getWritableDatabase();
		count = sqlDB.delete(TABLE_NAME, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;    
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		// get database to insert records
		sqlDB = dbHelper.getWritableDatabase();
		// insert record in user table and get the row number of recently inserted record
		long rowId = sqlDB.insert(TABLE_NAME, "", contentvalues);
		if (rowId > 0) {
			Uri rowUri = ContentUris.appendId(MovieDefinitions.MovieDefinition.CONTENT_URI.buildUpon(), rowId).build();
			getContext().getContentResolver().notifyChange(rowUri, null);
			return rowUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return (dbHelper == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		qb.setTables(TABLE_NAME);
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues contentvalues, String selection, String[] selectionArgs) {
		int count=0;
		// get database to update records
		sqlDB = dbHelper.getWritableDatabase();
		count = sqlDB.update(TABLE_NAME, contentvalues, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;    
	}
}