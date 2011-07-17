package com.FilmDb;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieAdapter extends CursorAdapter {

	// used to keep selected position in ListView
	private int mTitleIndex;
	private int mYearIndex;
	private int mWatchedIndex;
	private Drawable icon_green;
	private Drawable icon_red;
	private Context mContext;
	private Cursor mCursor;

	public MovieAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
		mCursor = c;
		if(c != null) {
			Resources res = mContext.getResources();
			mTitleIndex = c.getColumnIndex(MovieDefinitions.MovieDefinition.KEY_TITLE);
			mYearIndex = c.getColumnIndex(MovieDefinitions.MovieDefinition.KEY_YEAR);
			mWatchedIndex = c.getColumnIndex(MovieDefinitions.MovieDefinition.KEY_WATCHED);
			icon_green = res.getDrawable(R.drawable.icon_green_v);
			icon_red = res.getDrawable(R.drawable.icon_red_v);
		}
	}
	
	public int getWatched(int pos) {
		mCursor.moveToPosition(pos);
		return mCursor.getInt(mWatchedIndex);
	}
	
	public String getTitle(int pos) {
		mCursor.moveToPosition(pos);
		return mCursor.getString(mTitleIndex);
	}
	
	class ViewHolder {
		TextView mTitleView;
		TextView mYearView;
		ImageView mWatchedView;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int watched = 0;
		ViewHolder holder;
		holder = (ViewHolder) view.getTag();

		holder.mTitleView.setText(cursor.getString(mTitleIndex));
		holder.mYearView.setText(cursor.getString(mYearIndex));
		watched = cursor.getInt(mWatchedIndex);
		if(watched == 1)
			holder.mWatchedView.setImageDrawable(icon_green);
		else holder.mWatchedView.setImageDrawable(icon_red);	
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v;
		ViewHolder holder;

			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.movie_row, null);
			
			holder = new ViewHolder();
			holder.mTitleView =(TextView) v.findViewById(R.id.title);
			holder.mYearView =(TextView) v.findViewById(R.id.year);
			holder.mWatchedView = (ImageView) v.findViewById(R.id.icon_watched);
			
			v.setTag(holder);
			return v;
	}
}