/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

import com.FilmDb.R;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import net.sf.jtmdb.Genre;
import net.sf.jtmdb.Movie;
import net.sf.jtmdb.MoviePoster;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class MovieAdd extends CustomWindow implements OnItemClickListener {

	private String movieTitle;
	private List<Movie> movies = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = this.getIntent().getExtras();
		movieTitle = b.getString("MovieTitle");

		setContentView(R.layout.movie_add);
		this.icon.setImageResource(R.drawable.icon_add);
		
		fillData();
	}
	
	private void fillData() {
		try {
			movies = Movie.search(movieTitle);
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			
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
				ListView lvMovieList = (ListView) findViewById(R.id.movieadd_list);
				lvMovieList.setEmptyView(findViewById(R.id.empty_movieaddlist));
				lvMovieList.setAdapter(adapter);
				lvMovieList.setOnItemClickListener(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void saveMovieToDb(Movie movie) {
		try {
			final String movieName = movie.getName();
			if (!movieExists(movieName)) {
				
				String genreHulpString = "";
				String posterHulpUrl = "";
				String trailerHulpString = null;
				
				for (Genre genre : movie.getGenres()) {
					genreHulpString += genre.getName();
					genreHulpString += " ";
				}
			
				final String genreString = genreHulpString;
				genreHulpString = null;

				Set<MoviePoster> poster = movie.getImages().posters;
				Iterator<MoviePoster> iter = poster.iterator();
				
				if (iter.hasNext()) {
					posterHulpUrl = iter.next().getLargestImage().toString();
				}
				
				final String posterurl = posterHulpUrl;
				posterHulpUrl = null;

				URL trailerurl = movie.getTrailer();
				if (trailerurl != null)
					trailerHulpString = trailerurl.toExternalForm();
				
				final String trailer = trailerHulpString;
				trailerHulpString = null;
				
				final String movieYear = Integer.toString(movie
						.getReleasedDate().getYear() + 1900);
				
				final String movieOverview = movie.getOverview();

				AlertDialog.Builder alert = new AlertDialog.Builder(this);

				alert.setTitle("Watched?");
				alert.setMessage("Did you watch \"" + movieName + "\" already?");

				alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						createMovie(movieName, movieYear, genreString,
								movieOverview, posterurl, trailer, true);
						setResult(RESULT_OK);
						finish();
					}
				});

				alert.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						createMovie(movieName, movieYear, genreString,
								movieOverview, posterurl, trailer, false);
						setResult(RESULT_OK);
						finish();
					}
				});

				alert.show();
			}
			else Toast.makeText(this, "Movie \"" + movieName + "\" already exists", Toast.LENGTH_SHORT).show();			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Movie selectedMovie = null;
		selectedMovie = movies.get(position);
		String title = selectedMovie.getName();
		final Movie movie = selectedMovie;
		selectedMovie = null;

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add Movie");
		alert.setMessage("Add movie \"" + title + "\"?");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				saveMovieToDb(movie);
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
}
