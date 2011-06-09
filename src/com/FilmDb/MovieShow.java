/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.FilmDb.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieShow extends Activity {

    private TextView mTitleText;
    private TextView mGenreText;
    private TextView mSynopsisText;
    private Long mRowId;
    private MovieDbAdapter mDbHelper;
    private Cursor movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new MovieDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.movie_show);
        setTitle(R.string.show_movie);

        mTitleText = (TextView) findViewById(R.id.title_show);
        mGenreText = (TextView) findViewById(R.id.genre);
        mSynopsisText = (TextView) findViewById(R.id.synopsis);

        Button confirmButton = (Button) findViewById(R.id.done);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(MovieDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(MovieDbAdapter.KEY_ROWID)
									: null;
		}

		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    private void populateFields() {
        if (mRowId != null) {
            movie = mDbHelper.fetchMovie(mRowId);
            startManagingCursor(movie);
            mTitleText.setText(movie.getString(
                    movie.getColumnIndexOrThrow(MovieDbAdapter.KEY_TITLE)));
            mGenreText.setText(movie.getString(
                    movie.getColumnIndexOrThrow(MovieDbAdapter.KEY_GENRE)));
            mSynopsisText.setText(movie.getString(
                    movie.getColumnIndexOrThrow(MovieDbAdapter.KEY_SYNOPSIS)));
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
    	                    movie.getColumnIndexOrThrow(MovieDbAdapter.KEY_POSTER)));
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
    	        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getString(
             movie.getColumnIndexOrThrow(MovieDbAdapter.KEY_TRAILER)))));
    	 }
    };
}