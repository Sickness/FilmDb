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

public class MovieAdd extends CustomWindow {

	private String movieTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = this.getIntent().getExtras();
		movieTitle = b.getString("MovieTitle");

		setContentView(R.layout.movie_add);
		
		fillData();
	}
	
	private void fillData() {
		try {
			final List<Movie> movieList = Movie.search(movieTitle);
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			
			if (!movieList.isEmpty()) {
				// Create an array to specify the fields we want to display in
				// the list (only TITLE)
				for (Movie movie : movieList) {
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
				lvMovieList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
						final Movie movie = movieList.get(position);
						String title = movie.getName();

						AlertDialog.Builder alert = new AlertDialog.Builder(MovieAdd.this);

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
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void saveMovieToDb(Movie movie) {
		try {
			final String movieName = movie.getName();
			if (!movieExists(movieName)) {	
				int ID = movie.getID();
				movie = Movie.getInfo(ID);
				
				StringBuilder genreBuilder = new StringBuilder("");
				Set<Genre> genres = movie.getGenres();
				Iterator<Genre> genreIterator = genres.iterator();
				while(genreIterator.hasNext()) {
					genreBuilder.append(genreIterator.next().getName());
					if(genreIterator.hasNext())
						genreBuilder.append(" - ");
				}			
				final String genreString = genreBuilder.toString();

				StringBuilder posterHulpUrl = new StringBuilder("");
				Set<MoviePoster> poster = movie.getImages().posters;
				Iterator<MoviePoster> iter = poster.iterator();	
				if (iter.hasNext()) {
					posterHulpUrl.append(iter.next().getLargestImage().toString());
				}				
				final String posterurl = posterHulpUrl.toString();
				
				StringBuilder trailerBuilder = new StringBuilder();
				URL trailerurl = movie.getTrailer();
				if (trailerurl != null)
					trailerBuilder.append(trailerurl.toExternalForm());
				
				final String trailer = trailerBuilder.toString();
				
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
}
