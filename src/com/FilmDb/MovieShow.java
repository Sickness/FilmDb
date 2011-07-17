/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.FilmDb.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MovieShow extends CustomWindow {

    private TextView mTitleText;
    private TextView mGenreText;
    private TextView mSynopsisText;
    private Long mRowId;
    private Cursor movie;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_show);
        setTitle(R.string.show_movie);

        mTitleText = (TextView) findViewById(R.id.title_show);
        mGenreText = (TextView) findViewById(R.id.genre);
        mSynopsisText = (TextView) findViewById(R.id.synopsis);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(MovieDefinitions.MovieDefinition.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(MovieDefinitions.MovieDefinition.KEY_ROWID)
									: null;
		}

		populateFields();
    }

    private void populateFields() {
        if (mRowId != null) {
            movie = fetchMovie(mRowId);
            startManagingCursor(movie);
            mTitleText.setText(movie.getString(
                    movie.getColumnIndexOrThrow(MovieDefinitions.MovieDefinition.KEY_TITLE)));
            mGenreText.setText(movie.getString(
                    movie.getColumnIndexOrThrow(MovieDefinitions.MovieDefinition.KEY_GENRE)));
            mSynopsisText.setText(movie.getString(
                    movie.getColumnIndexOrThrow(MovieDefinitions.MovieDefinition.KEY_SYNOPSIS)));
            new FetchPosterTask().execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    public Bitmap getRemoteImage(final String url) {
        try {
        	URL aURL = new URL(url);
            final URLConnection conn = aURL.openConnection();
            conn.connect();
            BitmapFactory.Options options;
            options=new BitmapFactory.Options();
        	options.inSampleSize = 4;
            final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            final Bitmap bm = BitmapFactory.decodeStream(bis, null, options);
            bis.close();
            return bm;
        } catch (IOException e) {}
        return null;
    }
    
    private class FetchPosterTask extends AsyncTask<String, Void, Void> {
    	   private final ProgressDialog dialog = new ProgressDialog(MovieShow.this);
    	   private Bitmap bm;
    		      // can use UI thread here
    		      protected void onPreExecute() {
    		         this.dialog.setMessage("Fetching image...");
    		         this.dialog.show();
    		      }
    		 
    	      // automatically done on worker thread (separate from UI thread)
    	      protected Void doInBackground(final String... args) {
    	    	  bm = getRemoteImage(movie.getString(
    	                    movie.getColumnIndexOrThrow(MovieDefinitions.MovieDefinition.KEY_POSTER)));
    	        return null;
    	      }
    	 
    	     // can use UI thread here
    	      protected void onPostExecute(final Void unused) {
    	        if (this.dialog.isShowing()) {
    	            this.dialog.dismiss();
    	         }
    	        ImageView iv = new ImageView(MovieShow.this);
    	        iv = (ImageView)findViewById(R.id.poster);
    	        iv.setOnClickListener(imageviewClicker);
    	        if(bm != null)
    	        	iv.setImageBitmap(bm);
    	        
    	      }
    	   }
    
    private View.OnClickListener imageviewClicker = new View.OnClickListener()
    {
    	 public void onClick(View v) {
    		 String trailer =  movie.getString(
    	             movie.getColumnIndexOrThrow(MovieDefinitions.MovieDefinition.KEY_TRAILER));
    		 if(trailer != null)
    	        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer)));
    		 else Toast.makeText(MovieShow.this, "No trailer available", Toast.LENGTH_SHORT).show();
    	 }
    };
}