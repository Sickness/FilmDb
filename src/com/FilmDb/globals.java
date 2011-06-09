/*
 * Copyright (C) 2011 Stijn Delarbre.
 */

package com.FilmDb;

public class globals {


    public static final int ACTIVITY_CREATE=0;
    public static final int ACTIVITY_SHOW=1;
    public static final int ACTIVITY_TRAILER=2;
    public static boolean sortTitle = true;
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

}
