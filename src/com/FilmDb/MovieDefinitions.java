package com.FilmDb;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieDefinitions {

    public static final String AUTHORITY = "com.FilmDb.MovieContentProvider";

    // BaseColumn contains _id.
    public static final class MovieDefinition implements BaseColumns {

        public static final Uri    CONTENT_URI = Uri.parse("content://" + AUTHORITY);

        // Table columns
        public static final String KEY_TITLE = "title";
        public static final String KEY_YEAR = "year";
        public static final String KEY_GENRE = "genre";
        public static final String KEY_SYNOPSIS = "synopsis";
        public static final String KEY_POSTER = "posterurl";
        public static final String KEY_TRAILER = "trailerurl";
        public static final String KEY_WATCHED = "watched";
        public static final String KEY_MOVIEID = "movieId";
        public static final String KEY_ROWID = "_id";
    }
}