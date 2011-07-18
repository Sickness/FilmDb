package com.FilmDb;

import java.io.File;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.ImageView;

public class CustomWindow extends Activity {
	// only used in this activity
	private String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FilmDb";
	
	// used by all activities
	protected TextView title;
	protected ImageView icon;
	protected Globals globals = new Globals();	
	protected String extendedFilepath = filepath + "/";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);     
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.movie_list);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);

		title = (TextView) findViewById(R.id.title);
		icon  = (ImageView) findViewById(R.id.icon);
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
	public void createMovie(int movieId, String title, String year, String directors, String actors, String genre, String synopsis, String posterurl, String trailerurl, boolean watched) {
		ContentValues initialValues = new ContentValues();   
		initialValues.put(MovieDefinitions.MovieDefinition.KEY_MOVIEID,movieId);
		initialValues.put(MovieDefinitions.MovieDefinition.KEY_TITLE, title);
		initialValues.put(MovieDefinitions.MovieDefinition.KEY_YEAR, year);
		initialValues.put(MovieDefinitions.MovieDefinition.KEY_DIRECTORS, directors);
		initialValues.put(MovieDefinitions.MovieDefinition.KEY_ACTORS, actors);
		initialValues.put(MovieDefinitions.MovieDefinition.KEY_GENRE, genre);
		initialValues.put(MovieDefinitions.MovieDefinition.KEY_SYNOPSIS, synopsis);
		initialValues.put(MovieDefinitions.MovieDefinition.KEY_POSTER, posterurl);
		initialValues.put(MovieDefinitions.MovieDefinition.KEY_TRAILER,trailerurl);
		initialValues.put(MovieDefinitions.MovieDefinition.KEY_WATCHED,watched);

		getContentResolver().insert(MovieDefinitions.MovieDefinition.CONTENT_URI, initialValues);
	}

	/**
	 * Delete the movie with the given rowId
	 * 
	 * @param rowId id of movie to delete
	 */
	public void deleteMovie(long rowId, int movieId) {
		File image = new File(filepath + "/" + movieId + ".png");
		image.delete();
		
		File sdCardPath = new File(filepath);
		if (sdCardPath.isDirectory()) {
		    String[] files = sdCardPath.list();
		    if (files.length == 0) {
		    	sdCardPath.delete();
		    }
		}
		Uri uri = MovieDefinitions.MovieDefinition.CONTENT_URI;
		getContentResolver().delete(uri, MovieDefinitions.MovieDefinition.KEY_ROWID + "=?", new String[]{Long.toString(rowId)});
	}
	
	/**
	 * Delete all movies
	 */
	public void deleteAllMovies() {		
		File sdCardPath = new File(filepath);
		DeleteRecursive(sdCardPath);
		Uri uri = MovieDefinitions.MovieDefinition.CONTENT_URI;
		getContentResolver().delete(uri, null, null);
	}

	/**
	 * Delete the movie with the given rowId
	 * 
	 * @param rowId id of movie to delete
	 */
	public void toggleWatchedMovie(long rowId, int previousValue) {
		Uri uri = MovieDefinitions.MovieDefinition.CONTENT_URI;
		ContentValues newValue = new ContentValues(); 
		newValue.put(MovieDefinitions.MovieDefinition.KEY_WATCHED, previousValue==1?0:1);
		getContentResolver().update(uri, newValue, MovieDefinitions.MovieDefinition.KEY_ROWID + "=?", new String[]{Long.toString(rowId)});
	}


	/**
	 * Return a Cursor over the list of all movies in the database
	 * 
	 * @return Cursor over all movies
	 */
	public Cursor fetchAllMovies() {

		String orderBy;
		if(globals.sortByTitle())
			orderBy = MovieDefinitions.MovieDefinition.KEY_TITLE;
		else orderBy = MovieDefinitions.MovieDefinition.KEY_YEAR + " DESC";

		String columns[] = new String[] { MovieDefinitions.MovieDefinition.KEY_ROWID, 
				MovieDefinitions.MovieDefinition.KEY_TITLE, MovieDefinitions.MovieDefinition.KEY_YEAR,
				MovieDefinitions.MovieDefinition.KEY_MOVIEID, MovieDefinitions.MovieDefinition.KEY_WATCHED};
		Uri myUri = MovieDefinitions.MovieDefinition.CONTENT_URI;
		Cursor cur = getContentResolver().query(myUri, columns, MovieDefinitions.MovieDefinition.KEY_GENRE + " like ?",
				new String[] {"%" + globals.getCurrentGenre() + "%"}, orderBy);
		Log.i("Fetch Movies", Integer.toString(cur.getCount()));
		return cur;
	}

	/**
	 * Return a Cursor over the list of all movies in the database
	 * 
	 * @return Cursor over all movies
	 */
	public Cursor fetchAllMovies(int watched) {

		String orderBy;
		if(globals.sortByTitle())
			orderBy = MovieDefinitions.MovieDefinition.KEY_TITLE;
		else orderBy = MovieDefinitions.MovieDefinition.KEY_YEAR + " DESC";

		String columns[] = new String[] { MovieDefinitions.MovieDefinition.KEY_ROWID, 
				MovieDefinitions.MovieDefinition.KEY_TITLE, MovieDefinitions.MovieDefinition.KEY_YEAR,
				MovieDefinitions.MovieDefinition.KEY_MOVIEID, MovieDefinitions.MovieDefinition.KEY_WATCHED};
		Uri myUri = MovieDefinitions.MovieDefinition.CONTENT_URI;
		Cursor cur = getContentResolver().query(myUri, columns, MovieDefinitions.MovieDefinition.KEY_GENRE + " like ? AND " 
				+ MovieDefinitions.MovieDefinition.KEY_WATCHED + "=?",
				new String[] {"%" + globals.getCurrentGenre() + "%",Integer.toString(watched)}, orderBy);
		return cur;
	}

	/**
	 * Return a Cursor positioned at the movie that matches the given rowId
	 * 
	 * @param rowId id of movie to retrieve
	 * @return Cursor positioned to matching movie, if found
	 * @throws SQLException if movie could not be found/retrieved
	 */
	public Cursor fetchMovie(long rowId) throws SQLException {

		String columns[] = new String[] { MovieDefinitions.MovieDefinition.KEY_ROWID, 
				MovieDefinitions.MovieDefinition.KEY_MOVIEID,
				MovieDefinitions.MovieDefinition.KEY_TITLE, MovieDefinitions.MovieDefinition.KEY_YEAR,
				MovieDefinitions.MovieDefinition.KEY_DIRECTORS, MovieDefinitions.MovieDefinition.KEY_ACTORS,
				MovieDefinitions.MovieDefinition.KEY_GENRE, MovieDefinitions.MovieDefinition.KEY_SYNOPSIS,
				MovieDefinitions.MovieDefinition.KEY_POSTER, MovieDefinitions.MovieDefinition.KEY_TRAILER,
				MovieDefinitions.MovieDefinition.KEY_WATCHED};
		Uri myUri = MovieDefinitions.MovieDefinition.CONTENT_URI;
		Cursor cur = managedQuery(myUri, columns, // Which columns to return
				MovieDefinitions.MovieDefinition.KEY_ROWID+"=?", // WHERE clause; which rows to return(all rows)
				new String[] {Long.toString(rowId)},// WHERE clause selection arguments (none)
				null // Order-by clause (ascending by name)

		);
		if (cur != null) {
			cur.moveToFirst();
		}
		return cur;
	}

	/**
	 * Checks if the movie already exists
	 * 
	 * @param movie ID
	 * @return boolean
	 */
	public boolean movieExists(int ID) {

		String columns[] = new String[] { MovieDefinitions.MovieDefinition.KEY_ROWID};
		Uri myUri = MovieDefinitions.MovieDefinition.CONTENT_URI;
		Cursor cur = managedQuery(myUri, columns, // Which columns to return
				MovieDefinitions.MovieDefinition.KEY_MOVIEID +"=?", // WHERE clause; which rows to return(all rows)
				new String[] {Integer.toString(ID)},// WHERE clause selection arguments (none)
				null // Order-by clause (ascending by name)

		);
		boolean exists = (cur.getCount() > 0);
		cur.close();
		return exists;

	}
	
	private void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
}

