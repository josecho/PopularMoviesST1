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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class PopularMovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private PopularMovieDbHelper mOpenHelper;

    static final int POPULAR_MOVIES = 100;
    static final int POPULAR_MOVIES_ITEM = 101;
    static final int VIDEOS = 200;
    private static final int VIDEOS_ITEM = 201;
    static final int FAVORITES_VIDEOS = 203;
    private static final int REVIEWS = 300;
    private static final int REVIEWS_ITEM = 301;

    private static final SQLiteQueryBuilder sPopularMovieSettingQueryBuilder;

    static{
        sPopularMovieSettingQueryBuilder = new SQLiteQueryBuilder();
        
        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sPopularMovieSettingQueryBuilder.setTables(
                PopularMovieContract.PopularMovieEntry.TABLE_NAME + " INNER JOIN " +
                        PopularMovieContract.VideosEntry.TABLE_NAME +
                        " ON " + PopularMovieContract.PopularMovieEntry.TABLE_NAME +
                        "." + PopularMovieContract.PopularMovieEntry._ID +
                        " = " + PopularMovieContract.VideosEntry.TABLE_NAME +
                        "." + PopularMovieContract.VideosEntry.COLUMN_POPULAR_MOVIE_ID);

    }

    private static final String sVideosSettingSelection =
            PopularMovieContract.VideosEntry.TABLE_NAME+
                    "." + PopularMovieContract.VideosEntry.COLUMN_POPULAR_MOVIE_ID+ " = ? ";

    private static final String favoritesVideosSelection =
            PopularMovieContract.VideosEntry.TABLE_NAME+
                    "." + PopularMovieContract.VideosEntry.COLUMN_FAVORITE+ " = 1 ";

    private Cursor getFavoriteVideos(Uri uri, String[] projection, String sortOrder){
        String selection = favoritesVideosSelection;
        return sPopularMovieSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );
    }

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopularMovieContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        matcher.addURI(authority, PopularMovieContract.PATH_POPULAR_MOVIES, POPULAR_MOVIES);

        matcher.addURI(PopularMovieContract.CONTENT_AUTHORITY, PopularMovieContract
                .PATH_POPULAR_MOVIES, POPULAR_MOVIES);
        matcher.addURI(PopularMovieContract.CONTENT_AUTHORITY, PopularMovieContract
                        .PATH_POPULAR_MOVIES + "/#",
                POPULAR_MOVIES_ITEM);

        matcher.addURI(PopularMovieContract.CONTENT_AUTHORITY, PopularMovieContract
                .PATH_VIDEO, VIDEOS);
        matcher.addURI(PopularMovieContract.CONTENT_AUTHORITY, PopularMovieContract
                        .PATH_VIDEO + "/#",VIDEOS_ITEM);
        matcher.addURI(authority, PopularMovieContract.PATH_VIDEO+ "/*",FAVORITES_VIDEOS);

        matcher.addURI(PopularMovieContract.CONTENT_AUTHORITY, PopularMovieContract
                .PATH_REVIEW, REVIEWS);
        matcher.addURI(PopularMovieContract.CONTENT_AUTHORITY, PopularMovieContract
                        .PATH_REVIEW + "/#",
                REVIEWS_ITEM);
        // 3) Return the new matcher!
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new PopularMovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases

            case POPULAR_MOVIES:
                return PopularMovieContract.PopularMovieEntry.CONTENT_TYPE;
            case POPULAR_MOVIES_ITEM:
                return PopularMovieContract.PopularMovieEntry.CONTENT_ITEM_TYPE;
            case VIDEOS:
                return PopularMovieContract.VideosEntry.CONTENT_TYPE;
            case VIDEOS_ITEM:
                return PopularMovieContract.VideosEntry.CONTENT_ITEM_TYPE;
            case FAVORITES_VIDEOS:
                return PopularMovieContract.VideosEntry.CONTENT_TYPE;
            case REVIEWS:
                return PopularMovieContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEWS_ITEM:
                return PopularMovieContract.ReviewsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.

        Cursor retCursor;
        String id;
        switch (sUriMatcher.match(uri)) {
            case POPULAR_MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopularMovieContract.PopularMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIDEOS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopularMovieContract.VideosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIDEOS_ITEM: {
                id = uri.getPathSegments().get(1);
                retCursor = getItem(PopularMovieContract.VideosEntry.TABLE_NAME,
                        id, projection, selection, selectionArgs, sortOrder);
                break;
            }

            case REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopularMovieContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEWS_ITEM: {
                id = uri.getPathSegments().get(1);
                retCursor = getItem(PopularMovieContract.ReviewsEntry.TABLE_NAME,
                        id, projection, selection, selectionArgs, sortOrder);
                break;
            }

            case FAVORITES_VIDEOS: {
                retCursor = getFavoriteVideos(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getItem(String tableName, String id, String[] projection, String selection,
                           String[]
                                   selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(tableName);

        if (id != null) {
            sqliteQueryBuilder.appendWhere("_id" + " = " + id);
        }

        Cursor cursor = sqliteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case POPULAR_MOVIES: {
                //normalizeDate(values);
                long _id = db.insert(PopularMovieContract.PopularMovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = PopularMovieContract.PopularMovieEntry.buildPopularMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEOS: {
                //normalizeDate(values);
                long _id = db.insert(PopularMovieContract.VideosEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = PopularMovieContract.VideosEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(PopularMovieContract.ReviewsEntry.TABLE_NAME, null,
                        values);
                if (_id > 0)
                    returnUri = PopularMovieContract.ReviewsEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case POPULAR_MOVIES:
                rowsDeleted = db.delete(
                        PopularMovieContract.PopularMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEOS:
                rowsDeleted = db.delete(
                        PopularMovieContract.VideosEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(PopularMovieContract.ReviewsEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case POPULAR_MOVIES:
                rowsUpdated = db.update(PopularMovieContract.PopularMovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VIDEOS:
                rowsUpdated = db.update(PopularMovieContract.VideosEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(PopularMovieContract.ReviewsEntry.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POPULAR_MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        long _id = db.insert(PopularMovieContract.PopularMovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}