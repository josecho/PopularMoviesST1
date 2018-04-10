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

import com.udacity.course.popularmoviesst1.app.data.PopularMovieContract.PopularMovieEntry;
import com.udacity.course.popularmoviesst1.app.data.PopularMovieContract.VideosEntry;
import com.udacity.course.popularmoviesst1.app.data.PopularMovieContract.ReviewsEntry;

/**
 * Manages a local database for popular movie data.
 */
class PopularMovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "popularMovies.db";

    public PopularMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + VideosEntry.TABLE_NAME + " (" +
                VideosEntry.COLUMN_VIDEO_ID + " TEXT PRIMARY KEY UNIQUE," +
                VideosEntry.COLUMN_POPULAR_MOVIE_ID + " INTEGER NOT NULL, " +
                VideosEntry.COLUMN_ISO_639_1 + " TEXT, " +
                VideosEntry.COLUMN_ISO_3166_1 + " TEXT, " +
                VideosEntry.COLUMN_KEY + " TEXT, " +
                VideosEntry.COLUMN_NAME + " TEXT, " +
                VideosEntry.COLUMN_SITE + " TEXT, " +
                VideosEntry.COLUMN_SIZE + " INTEGER, " +
                VideosEntry.COLUMN_TYPE + " TEXT " +
                " );";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry.COLUMN_REVIEW_ID + " TEXT PRIMARY KEY UNIQUE," +
                ReviewsEntry.COLUMN_POPULAR_MOVIE_ID + " INTEGER NOT NULL, " +
                ReviewsEntry.COLUMN_AUTHOR + " TEXT, " +
                ReviewsEntry.COLUMN_CONTENT + " TEXT, " +
                ReviewsEntry.COLUMN_URL + " TEXT " +
                " );";

        final String SQL_CREATE_POPULAR_MOVIE_TABLE = "CREATE TABLE " + PopularMovieEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.
                PopularMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopularMovieEntry.COLUMN_POPULAR_MOVIE_ID + " INTEGER NOT NULL, " +
                PopularMovieEntry.COLUMN_ORIGINAL_TITLE + " STRING, " +
                PopularMovieEntry.COLUMN_POSTER_MAP + " STRING, " +
                PopularMovieEntry.COLUMN_OVERWIEW + " STRING, " +
                PopularMovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                PopularMovieEntry.COLUMN_RELEASE_DATE + " STRING, " +
                // Set up the location column as a foreign key to VIDEOS table.
                " FOREIGN KEY (" + PopularMovieEntry.COLUMN_POPULAR_MOVIE_ID + ") REFERENCES " +
                VideosEntry.TABLE_NAME + " (" + VideosEntry.COLUMN_POPULAR_MOVIE_ID + "), " +
                // Set up the location column as a foreign key to VIDEOS table.
                " FOREIGN KEY (" + PopularMovieEntry.COLUMN_POPULAR_MOVIE_ID + ") REFERENCES " +
                ReviewsEntry.TABLE_NAME + " (" + ReviewsEntry.COLUMN_POPULAR_MOVIE_ID + ") " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideosEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
