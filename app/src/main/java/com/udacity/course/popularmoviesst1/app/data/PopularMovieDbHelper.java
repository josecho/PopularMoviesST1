/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.udacity.course.popularmoviesst1.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.udacity.course.popularmoviesst1.app.data.PopularMovieContract.PopularMovieEntry;
import com.udacity.course.popularmoviesst1.app.data.PopularMovieContract.ReviewsEntry;
import com.udacity.course.popularmoviesst1.app.data.PopularMovieContract.VideosEntry;

/**
 * Manages a local database for popular movie data.
 */
class PopularMovieDbHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = PopularMovieDbHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "peliculas.db";

    public PopularMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "Creating tables ****************************** ");

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + VideosEntry.TABLE_NAME + " (" +
                VideosEntry._ID + " TEXT PRIMARY KEY UNIQUE," +
                VideosEntry.COLUMN_POPULAR_MOVIE_ID + " INTEGER NOT NULL, " +
                VideosEntry.COLUMN_ISO_639_1 + " TEXT, " +
                VideosEntry.COLUMN_ISO_3166_1 + " TEXT, " +
                VideosEntry.COLUMN_KEY + " TEXT, " +
                VideosEntry.COLUMN_NAME + " TEXT, " +
                VideosEntry.COLUMN_SITE + " TEXT, " +
                VideosEntry.COLUMN_SIZE + " INTEGER, " +
                VideosEntry.COLUMN_TYPE + " TEXT, " +
                VideosEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY (" + VideosEntry.COLUMN_POPULAR_MOVIE_ID + ") REFERENCES " +
                PopularMovieEntry.TABLE_NAME + " (" + PopularMovieEntry._ID + ") ON DELETE CASCADE" +
                " );";

        Log.d(LOG_TAG, SQL_CREATE_VIDEOS_TABLE);

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry._ID + " TEXT PRIMARY KEY UNIQUE," +
                ReviewsEntry.COLUMN_POPULAR_MOVIE_ID + " INTEGER NOT NULL, " +
                ReviewsEntry.COLUMN_AUTHOR + " TEXT, " +
                ReviewsEntry.COLUMN_CONTENT + " TEXT, " +
                ReviewsEntry.COLUMN_URL + " TEXT, " +
                "FOREIGN KEY (" + ReviewsEntry.COLUMN_POPULAR_MOVIE_ID + ") REFERENCES " +
                PopularMovieEntry.TABLE_NAME + " (" + PopularMovieEntry._ID + ") ON DELETE CASCADE" +
                " );";

        final String SQL_CREATE_POPULAR_MOVIE_TABLE = "CREATE TABLE " + PopularMovieEntry.TABLE_NAME + " (" +
                PopularMovieEntry._ID + " INTEGER PRIMARY KEY UNIQUE," +
                PopularMovieEntry.COLUMN_ORIGINAL_TITLE + " STRING, " +
                PopularMovieEntry.COLUMN_POSTER_MAP + " STRING, " +
                PopularMovieEntry.COLUMN_OVERWIEW + " STRING, " +
                PopularMovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                PopularMovieEntry.COLUMN_RELEASE_DATE + " STRING, " +
                PopularMovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0 " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Updating table from********************************************************** " + oldVersion + " to " + newVersion);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideosEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
