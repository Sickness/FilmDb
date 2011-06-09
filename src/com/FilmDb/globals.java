/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

public class globals {


	public static enum GenreEnum
	{
		ALL,
		ACTION,
		THRILLER,
		HORROR,
		ROMANCE,
		DRAMA,
		COMEDY
	}

	public static final int ACTIVITY_CREATE=0;
	public static final int ACTIVITY_SHOW=1;
	public static final int ACTIVITY_TRAILER=2;
	public static boolean sortTitle = true;
	public static GenreEnum currentGenre = GenreEnum.ALL;
	private static globals instance;

	static {
		instance = new globals();
	}

	private globals() {
	}

	public static globals getInstance() {
		return globals.instance;
	}

	public static boolean sortByTitle()
	{
		return sortTitle;
	}

	public static void toggleSort()
	{
		sortTitle = !sortTitle;
	}

	public static GenreEnum nextGenre()
	{
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
	
	public static GenreEnum previousGenre()
	{
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
}
