/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

import android.app.Application;

// TODO add support for watched or not-watched movies
public class Globals extends Application {


	private static enum GenreEnum
	{
		ALL,
		ACTION,
		THRILLER,
		HORROR,
		ROMANCE,
		DRAMA,
		COMEDY
	}

	public final CharSequence[] genreItems = {"All", "Action", "Thriller", "Horror", "Romance", "Drama", "Comedy"};

	public final int ACTIVITY_CREATE=0;
	public final int ACTIVITY_SHOW=1;
	public static final int ACTIVITY_TRAILER=2;
	private static boolean sortTitle = true;
	private static boolean swipeLeft = true;
	private static GenreEnum currentGenre = GenreEnum.ALL;

	public boolean sortByTitle()
	{
		return sortTitle;
	}

	public void toggleSort()
	{
		sortTitle = !sortTitle;
	}


	public static boolean getSwipeLeft()
	{
		return swipeLeft;
	}

	public GenreEnum nextGenre()
	{
		swipeLeft = true;
		switch(currentGenre)
		{
		case ALL:
			currentGenre = GenreEnum.ACTION;
			break;
		case ACTION:
			currentGenre = GenreEnum.THRILLER;
			break;
		case THRILLER:
			currentGenre = GenreEnum.HORROR;
			break;
		case HORROR:
			currentGenre = GenreEnum.ROMANCE;
			break;
		case ROMANCE:
			currentGenre = GenreEnum.DRAMA;
			break;
		case DRAMA:
			currentGenre = GenreEnum.COMEDY;
			break;
		default:
			currentGenre = GenreEnum.ALL;
		}
		return currentGenre;
	}

	public GenreEnum previousGenre()
	{
		swipeLeft = false;
		switch(currentGenre)
		{
		case ALL:
			currentGenre = GenreEnum.COMEDY;
			break;
		case COMEDY:
			currentGenre = GenreEnum.DRAMA;
			break;
		case DRAMA:
			currentGenre = GenreEnum.ROMANCE;
			break;
		case ROMANCE:
			currentGenre = GenreEnum.HORROR;
			break;
		case HORROR:
			currentGenre = GenreEnum.THRILLER;
			break;
		case THRILLER:
			currentGenre = GenreEnum.ACTION;
			break;
		default:
			currentGenre = GenreEnum.ALL;
		}
		return currentGenre;
	}

	public void setGenre(int genre)
	{
		switch(genre)
		{
		case 0:
			currentGenre = GenreEnum.ALL;
			return;
		case 1:
			currentGenre = GenreEnum.ACTION;
			return;
		case 2:
			currentGenre = GenreEnum.THRILLER;
			return;
		case 3:
			currentGenre = GenreEnum.HORROR;
			return;
		case 4:
			currentGenre = GenreEnum.ROMANCE;
			return;
		case 5:
			currentGenre = GenreEnum.DRAMA;
			return;
		case 6:
			currentGenre = GenreEnum.COMEDY;
			return;
		default:
			return;

		}
	}

	public String getCurrentGenre()
	{
		switch(currentGenre)
		{
		case ACTION:
			return "Action";
		case COMEDY:
			return "Comedy";
		case DRAMA:
			return "Drama";
		case ROMANCE:
			return "Romance";
		case HORROR:
			return "Horror";
		case THRILLER:
			return "Thriller";
		default:
			return "";
		}
	}
}
