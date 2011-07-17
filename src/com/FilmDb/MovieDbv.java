/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

import net.sf.jtmdb.GeneralSettings;

import com.FilmDb.R;

import android.widget.AdapterView.AdapterContextMenuInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

// TODO add extra buttons to listview: delete/view + change imageview to button --> remove context menu (only buttons used)
// TODO Maybe add support to toggle between ascending and descending
// TODO Add activity to view by first letter of title --> with a clickable gallery or scrollview or something on top
// TODO download images to sd-card (movieAdd) and load from sd-card (movieShow) -- on delete: remove image from sd-card
public class MovieDbv extends CustomWindow implements OnItemClickListener {
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int SORT_ID = Menu.FIRST + 2;
	private static final int WATCHED_ID = Menu.FIRST + 3;
	private static final int VISIBILITY_ID = Menu.FIRST + 4;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ViewFlipper viewFlipper;
	
	private MovieAdapter movieAdapter;
	
	private Globals globals;
	private int checkedVisibility = 0;

	private TextView CategoryText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);        
		GeneralSettings.setApiKey("33fc4693e8d0e1e72fc38c09cc0817d3");
		globals = new Globals();
		CategoryText = (TextView)findViewById(R.id.category);
		viewFlipper = (ViewFlipper)findViewById(R.id.flipper);
		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
		gestureDetector = new GestureDetector(new SwipeEventHandler());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
		fillData();
	}

	private void fillData() {
		String category = globals.getCurrentGenre();
		if (category == "")
			category = "All";
		CategoryText.setText(category + " Movies");
		Cursor moviesCursor;
		if(checkedVisibility == 0)
			moviesCursor = fetchAllMovies();
		else moviesCursor = fetchAllMovies(checkedVisibility - 1);
		startManagingCursor(moviesCursor);
		movieAdapter = 
			new MovieAdapter(this,  moviesCursor);
		ListView lvMovies = (ListView) this.findViewById(R.id.movielist);
		lvMovies.setEmptyView(findViewById(R.id.empty_movielist));
		lvMovies.setAdapter(movieAdapter);
		lvMovies.setOnItemClickListener(this);
		registerForContextMenu(lvMovies);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		menu.add(0, SORT_ID, 0, R.string.menu_sort);
		menu.add(0,VISIBILITY_ID,0,R.string.menu_visibility);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case INSERT_ID:
			createMovie();
			return true;
		case SORT_ID:
			globals.toggleSort();
			fillData();
			return true;
		case VISIBILITY_ID:
			final CharSequence[] items = {"All", "To be watched", "Watched"};
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("Pick visibility-mode");
	        builder.setItems(items, new DialogInterface.OnClickListener(){
	            public void onClick(DialogInterface dialogInterface, int item) {
	            	checkedVisibility = item;
	            	fillData();
	                return;
	            }
	        });
	        builder.create().show();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, WATCHED_ID, 0, R.string.menu_watched);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
		case DELETE_ID:		
			deleteMovie(info.id);
			fillData();
			return true;
		case WATCHED_ID:
			toggleWatchedMovie(info.id, (movieAdapter.getWatched(info.position)==1?0:1) );
			fillData();
			return true;
			
		}
		return super.onContextItemSelected(item);
	}

	private void createMovie() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add Movie");
		alert.setMessage("Please insert movie title:");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(input.getText().length() != 0)
				{
					String value = input.getText().toString();
					Intent i = new Intent(MovieDbv.this, MovieAdd.class);
					Bundle b = new Bundle();
					b.putString("MovieTitle", value);
					i.putExtras(b);
					startActivityForResult(i, globals.ACTIVITY_CREATE);
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	class SwipeEventHandler extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					viewFlipper.setInAnimation(slideLeftIn);
					viewFlipper.setOutAnimation(slideLeftOut);
					viewFlipper.showNext();
					globals.nextGenre();
					fillData();
				}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					viewFlipper.setInAnimation(slideRightIn);
					viewFlipper.setOutAnimation(slideRightOut);
					viewFlipper.showPrevious();
					globals.previousGenre();
					fillData();
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return gestureDetector.onTouchEvent(event);
	}

	@Override 
	public boolean dispatchTouchEvent(MotionEvent ev){
		gestureDetector.onTouchEvent(ev);
		super.dispatchTouchEvent(ev); 
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		Intent i = new Intent(this, MovieShow.class);
		i.putExtra(MovieDefinitions.MovieDefinition.KEY_ROWID, id);
		startActivityForResult(i, globals.ACTIVITY_SHOW);		
	} 
}
