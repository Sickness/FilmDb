/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

import net.sf.jtmdb.GeneralSettings;

import com.FilmDb.R;

import android.widget.AdapterView.AdapterContextMenuInfo;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MovieDbv3 extends ListActivity {
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int SORT_ID = Menu.FIRST + 2;

    private MovieDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        GeneralSettings.setApiKey("33fc4693e8d0e1e72fc38c09cc0817d3");
        setContentView(R.layout.movie_list);
        mDbHelper = new MovieDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }

    private void fillData() {
        Cursor moviesCursor = mDbHelper.fetchAllMovies();
        startManagingCursor(moviesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{MovieDbAdapter.KEY_TITLE, MovieDbAdapter.KEY_YEAR};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.title,R.id.year};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter movies = 
            new SimpleCursorAdapter(this, R.layout.movie_row, moviesCursor, from, to);
        setListAdapter(movies);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        menu.add(0, SORT_ID, 0, R.string.menu_sort);
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
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteMovie(info.id);
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
    				Intent i = new Intent(MovieDbv3.this, MovieAdd.class);
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, MovieShow.class);
        i.putExtra(MovieDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, globals.ACTIVITY_SHOW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
