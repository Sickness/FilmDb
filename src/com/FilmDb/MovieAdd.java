/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

import com.FilmDb.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import net.sf.jtmdb.Genre;
import net.sf.jtmdb.Movie;
import net.sf.jtmdb.MoviePoster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;

public class MovieAdd extends ListActivity {

	private MovieDbAdapter mDbHelper;
	private String movieTitle;
	private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new MovieDbAdapter(this);
		mDbHelper.open();

		Bundle b = this.getIntent().getExtras();
		movieTitle = b.getString("MovieTitle");

		setContentView(R.layout.movie_add);
		setTitle(R.string.menu_insert);

		fillData();
	}

	private void fillData() {
		List<Movie> movies = null;
		try {
			movies = Movie.search(movieTitle);

			if (!movies.isEmpty()) {
				// Create an array to specify the fields we want to display in
				// the list (only TITLE)
				for (Movie movie : movies) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("ID", Integer.toString(movie.getID()));
					map.put("title",
							movie.getName()
									+ " ("
									+ Integer.toString(movie.getReleasedDate()
											.getYear() + 1900) + ")");
					list.add(map);
				}

				// the from array specifies which keys from the map
				// we want to view in our ListView
				String[] from = { "title" };

				// and an array of the fields we want to bind those fields to
				// (in this case just text1)
				int[] to = new int[] { R.id.movie_title };

				// create the adapter and assign it to the listview
				SimpleAdapter adapter = new SimpleAdapter(
						this.getApplicationContext(), list,
						R.layout.movie_add_row, from, to);
				setListAdapter(adapter);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		HashMap<String, String> map = list.get(position);
		final int ID = Integer.parseInt(map.get("ID"));
		String title = map.get("title");

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add Movie");
		alert.setMessage("Add movie \"" + title + "\"?");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				saveMovieToDb(ID);
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();

	}

	protected void saveMovieToDb(int iD) {
		try {
			Movie movie = Movie.getInfo(iD);
			Toast.makeText(this, movie.getName(), Toast.LENGTH_SHORT).show();
			if (!mDbHelper.movieExists(movie.getName())) {
				String genreString = "";
				for (Genre genre : movie.getGenres()) {
					genreString += genre.getName();
					genreString += " ";
				}
				Set<MoviePoster> poster = movie.getImages().posters;
				Iterator<MoviePoster> iter = poster.iterator();
				String posterurl = "";
				if (iter.hasNext()) {
					posterurl = iter.next().getLargestImage().toString();
				}

				mDbHelper.createMovie(movie.getName(), Integer.toString(movie
						.getReleasedDate().getYear() + 1900), genreString,
						movie.getOverview(), posterurl, movie.getTrailer()
								.toString());
			}

			setResult(RESULT_OK);
			finish();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
