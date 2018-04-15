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

    //private WeatherDbHelper mOpenHelper;
    private PopularMovieDbHelper mOpenHelper;

    static final int POPULAR_MOVIES = 100;
    static final int WEATHER_WITH_LOCATION = 101;
    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    //static final int LOCATION = 300;
    static final int VIDEO = 300;
    static final int FAVORITES_VIDEOS = 301;

    //private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
    private static final SQLiteQueryBuilder sPopularMovieSettingQueryBuilder;


    static{
        //sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sPopularMovieSettingQueryBuilder = new SQLiteQueryBuilder();
        
        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sPopularMovieSettingQueryBuilder.setTables(
                PopularMovieContract.PopularMovieEntry.TABLE_NAME + " INNER JOIN " +
                        PopularMovieContract.VideosEntry.TABLE_NAME +
                        " ON " + PopularMovieContract.PopularMovieEntry.TABLE_NAME +
                        "." + PopularMovieContract.PopularMovieEntry.COLUMN_POPULAR_MOVIE_ID +
                        " = " + PopularMovieContract.VideosEntry.TABLE_NAME +
                        "." + PopularMovieContract.VideosEntry.COLUMN_POPULAR_MOVIE_ID);

    }


    /*//location.location_setting = ?
    private static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";*/
    //video = ?
    private static final String sVideosSettingSelection =
            PopularMovieContract.VideosEntry.TABLE_NAME+
                    "." + PopularMovieContract.VideosEntry.COLUMN_POPULAR_MOVIE_ID+ " = ? ";

    //location.location_setting = ? AND date >= ?
    /*private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";*/

    /*private static final String sLocationSettingAndDaySelection =
            PopularMovieContract.VideosEntry.TABLE_NAME+
                    "." + WeatherContractte.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";*/

   /* private static final String favoritesVideosSelection =
            PopularMovieContract.VideosEntry.TABLE_NAME+
                    "." + PopularMovieContract.VideosEntry.COLUMN_FAVORITE+ " = 1 ";*/

   /* private Cursor getFavoriteVideos(Uri uri, String[] projection, String sortOrder){
        String selection = favoritesVideosSelection;
        return sPopularMovieSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );

    }*/

   /* private Cursor getPopularMoviesSetting(Uri uri, String[] projection, String sortOrder){

    }*/

    /*private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }*/



    /*private Cursor getWeatherByLocationSettingAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }*/

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.


        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.


        // 3) Return the new matcher!
        return null;
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

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
//            case WEATHER_WITH_LOCATION_AND_DATE:
//            case WEATHER_WITH_LOCATION:
            case POPULAR_MOVIES:
                //return WeatherContract.WeatherEntry.CONTENT_TYPE;
                return PopularMovieContract.PopularMovieEntry.CONTENT_TYPE;
            case VIDEO:
                //return WeatherContract.LocationEntry.CONTENT_TYPE;
                return PopularMovieContract.VideosEntry.CONTENT_TYPE;
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
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            /*case WEATHER_WITH_LOCATION_AND_DATE:
            {
                retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
                break;
            }*/
            // "weather/*"
            /*case WEATHER_WITH_LOCATION: {
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            }*/
            /*case FAVORITES_VIDEOS: {
                retCursor = getFavoriteVideos(uri, projection, sortOrder);
            }*/
            // "weather"
            case POPULAR_MOVIES: {
                retCursor = null;
                break;
            }
            // "location"
            case VIDEO: {
                retCursor = null;
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database

        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.

        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.

        // Student: return the actual rows deleted
        return 0;
    }

   /* private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
            values.put(WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.normalizeDate(dateValue));
        }
    }*/

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        return 0;
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