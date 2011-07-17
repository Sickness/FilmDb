/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.FilmDb.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MovieShow extends CustomWindow implements OnClickListener {

    private TextView mTitleText;
    private TextView mGenreText;
    private TextView mSynopsisText;
    private Button mWatched;
    private Button mDelete;
    private ImageView iv;
    private Long mRowId;
    private int movieId;
    private Cursor movie;
    private int watched;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_show);
        setTitle(R.string.show_movie);

        mTitleText = (TextView) findViewById(R.id.title_show);
        mGenreText = (TextView) findViewById(R.id.genre);
        mSynopsisText = (TextView) findViewById(R.id.synopsis);
        mWatched = (Button) findViewById(R.id.watched_show);
        mDelete = (Button) findViewById(R.id.delete_show);

        iv = (ImageView)findViewById(R.id.poster);
        iv.setOnClickListener(imageviewClicker);

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
            watched = movie.getInt(movie.getColumnIndexOrThrow(MovieDefinitions.MovieDefinition.KEY_WATCHED));
            mWatched.setBackgroundResource(watched==1?R.drawable.icon_green_v:R.drawable.icon_red_v);
            mWatched.setOnClickListener(this);
            mDelete.setOnClickListener(this);
            
            movieId = movie.getInt(movie.getColumnIndexOrThrow(MovieDefinitions.MovieDefinition.KEY_MOVIEID));
            File path = new File(extendedFilepath + movieId  + ".png");
            Bitmap image = getSdcardImage(path);
            if(image == null)
            	new FetchPosterTask().execute();
            else iv.setImageBitmap(getRoundedCornerBitmap(image,12));
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
    
    public Bitmap getSdcardImage(final File path) {
        try {
            final FileInputStream bis = new FileInputStream(path);
            final Bitmap bm = BitmapFactory.decodeStream(bis, null, null);
            bis.close();
            return bm;
        } catch (IOException e) {}
        return null;
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
    	        if(bm != null)
    	        	iv.setImageBitmap(getRoundedCornerBitmap(bm,12));
    	        
    	      }
    	   }
    
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	         bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);

	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    return output;
	    }
    
    private View.OnClickListener imageviewClicker = new View.OnClickListener()
    {
    	 public void onClick(View v) {
    		 String trailer =  movie.getString(
    	             movie.getColumnIndexOrThrow(MovieDefinitions.MovieDefinition.KEY_TRAILER));
    		 if(trailer != null && isOnline())
    	        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer)));
    		 else Toast.makeText(MovieShow.this, "No trailer available", Toast.LENGTH_SHORT).show();
    	 }
    };

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.watched_show:
			toggleWatchedMovie(mRowId, watched);
			watched = (watched==1)?0:1;
			mWatched.setBackgroundResource(watched==1?R.drawable.icon_green_v:R.drawable.icon_red_v);
			break;
		case R.id.delete_show:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Delete Movie?");

			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					deleteMovie(mRowId, movieId);
					setResult(RESULT_OK);
					finish();
				}
			});

			alert.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});

			alert.show();
		}		
	}
}