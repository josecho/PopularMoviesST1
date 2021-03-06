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

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

/*
    Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
    that at least the basic functionality has been implemented correctly.

    Students: Uncomment the tests in this class as you implement the functionality in your
    ContentProvider to make sure that you've implemented things reasonably correctly.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    static final Integer TEST_POPULAR_MOVIE_ID = 269149;
    static final String TEST_VIDEO_ID = "571cb2c0c3a36843150006ed";

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                PopularMovieContract.VideosEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from PopularMovie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                PopularMovieContract.VideosEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Videos table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecordsFromDB() {
        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(PopularMovieContract.PopularMovieEntry.TABLE_NAME, null, null);
        db.delete(PopularMovieContract.VideosEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        //deleteAllRecordsFromDB();
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // PopularMovieProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                PopularMovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: PopularMovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + PopularMovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, PopularMovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: PopularMovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        // content://com.udacity.course.popularmoviesst1.app/popularmovies/
        String type = mContext.getContentResolver().getType(PopularMovieContract.PopularMovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.udacity.course.popularmoviesst1.app/popularmovies
        assertEquals("Error: the PopularMovieEntry CONTENT_URI should return PopularMovieEntry.CONTENT_TYPE",
                PopularMovieContract.PopularMovieEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(PopularMovieContract.VideosEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals("Error: the VideosEntry CONTENT_URI should return VideosEntry.CONTENT_TYPE",
                PopularMovieContract.VideosEntry.CONTENT_TYPE, type);

       /* type = mContext.getContentResolver().getType(
                PopularMovieContract.VideosEntry.buildFavoriteVideos("favorites"));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the VideosEntry CONTENT_URI favorite videos should return VideosEntry.CONTENT_TYPE",
                PopularMovieContract.VideosEntry.CONTENT_TYPE, type);*/
    }

    public void testPopularMoviesQuery() {

        // insert our test records into the database
        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createPopularMoviesValues(TEST_POPULAR_MOVIE_ID);
        long popularMovieRowId = TestUtilities.insertPopularMoviesValues(mContext);
        assertTrue("Unable to Insert PopularMovie into the Database", popularMovieRowId != -1);

        Cursor popularMoviesCursor = mContext.getContentResolver().query(
                PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testPopularMoviesQuery", popularMoviesCursor, testValues);
    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicVideosQuery() {

        ContentValues testValues = TestUtilities.createPopularMoviesValues(TEST_POPULAR_MOVIE_ID);
        long locationRowId = TestUtilities.insertPopularMoviesValues(mContext);
        assertTrue("Unable to Insert PopularMovie into the Database", locationRowId != -1);
        Cursor popularMoviesCursor = mContext.getContentResolver().query(
                PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        popularMoviesCursor.moveToFirst();
        //public static final String COLUMN_POPULAR_MOVIE_ID = "movie_id";
        Long movie_id = popularMoviesCursor.getLong(0);
        assertEquals(TestUtilities.TEST_POPULAR_MOVIE_ID,Integer.valueOf(movie_id.intValue()));
        TestUtilities.validateCursor("testBasicVideosQuery", popularMoviesCursor, testValues);


        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues videosValues = TestUtilities.createVideosValues(TEST_VIDEO_ID, movie_id);
        long weatherRowId = db.insert(PopularMovieContract.VideosEntry.TABLE_NAME, null, videosValues);
        assertTrue("Unable to Insert VideosEntry into the Database", weatherRowId != -1);
        db.close();
        // Test the basic content provider query
        Cursor videosCursor = mContext.getContentResolver().query(
                PopularMovieContract.VideosEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", videosCursor, videosValues);
        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    videosCursor.getNotificationUri(), PopularMovieContract.VideosEntry.CONTENT_URI);
        }
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if your location queries are
        performing correctly.
     */
//    public void testBasicLocationQueries() {
//        // insert our test records into the database
//        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
//        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);
//
//        // Test the basic content provider query
//        Cursor locationCursor = mContext.getContentResolver().query(
//                LocationEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null
//        );
//
//        // Make sure we get the correct cursor out of the database
//        TestUtilities.validateCursor("testBasicLocationQueries, location query", locationCursor, testValues);
//
//        // Has the NotificationUri been set correctly? --- we can only test this easily against API
//        // level 19 or greater because getNotificationUri was added in API level 19.
//        if ( Build.VERSION.SDK_INT >= 19 ) {
//            assertEquals("Error: Location Query did not properly set NotificationUri",
//                    locationCursor.getNotificationUri(), LocationEntry.CONTENT_URI);
//        }
//    }

    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */


    public void testUpdatePopularMovies() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createPopularMoviesValues(TestUtilities.TEST_POPULAR_MOVIE_ID);

        Uri locationUri = mContext.getContentResolver().
                insert(PopularMovieContract.PopularMovieEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(PopularMovieContract.PopularMovieEntry._ID, locationRowId);
        updatedValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_ORIGINAL_TITLE, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(PopularMovieContract.PopularMovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                PopularMovieContract.PopularMovieEntry.CONTENT_URI, updatedValues, PopularMovieContract.PopularMovieEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                null,   // projection
                PopularMovieContract.PopularMovieEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating POPULARMOVIE entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testUpdateVideos() {
        // Create a new map of values, where column names are the keys
        ContentValues popularMoviesValues = TestUtilities.createPopularMoviesValues(TestUtilities.TEST_POPULAR_MOVIE_ID);

        Uri popularMoviesUri = mContext.getContentResolver().
                insert(PopularMovieContract.PopularMovieEntry.CONTENT_URI, popularMoviesValues);
        long locationRowId = ContentUris.parseId(popularMoviesUri);

        ContentValues videosValues = TestUtilities.createVideosValues(TestUtilities.TEST_VIDEO_ID,TestUtilities.TEST_POPULAR_MOVIE_ID);
        Uri videosUri = mContext.getContentResolver().
                insert(PopularMovieContract.VideosEntry.CONTENT_URI, videosValues);
        long vidoeosRowId = ContentUris.parseId(videosUri);

        // Verify we got a row back.
        assertTrue(vidoeosRowId != -1);
        Log.d(LOG_TAG, "New row id: " + vidoeosRowId);

        ContentValues updatedValues = new ContentValues(videosValues);
        //updatedValues.put(PopularMovieContract.VideosEntry._ID, vidoeosRowId);
        updatedValues.put(PopularMovieContract.VideosEntry.COLUMN_FAVORITE, 1);

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(PopularMovieContract.VideosEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                PopularMovieContract.VideosEntry.CONTENT_URI, updatedValues, PopularMovieContract.VideosEntry._ID + "= ?",
                new String[] { Long.toString(vidoeosRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PopularMovieContract.VideosEntry.CONTENT_URI,
                null,   // projection
                PopularMovieContract.VideosEntry._ID + " = " + vidoeosRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateVideos.  Error validating VIDEOS entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createPopularMoviesValues(TestUtilities.TEST_POPULAR_MOVIE_ID);

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PopularMovieContract.PopularMovieEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(PopularMovieContract.PopularMovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating PopularMovieEntry.",
                cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createVideosValues(TestUtilities.TEST_VIDEO_ID,TestUtilities.TEST_POPULAR_MOVIE_ID);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(PopularMovieContract.VideosEntry.CONTENT_URI, true, tco);

        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(PopularMovieContract.VideosEntry.CONTENT_URI, weatherValues);
        assertTrue(weatherInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                PopularMovieContract.VideosEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating VideosEntry insert.",
                weatherCursor, weatherValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        //weatherValues.putAll(testValues);

        // Get the joined Weather and Location data
       /* weatherCursor = mContext.getContentResolver().query(
                PopularMovieContract.VideosEntry.buildFavoriteVideos("favorite"),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined VideosEntry.buildFavoriteVideos Data.",
                weatherCursor, weatherValues);*/




    }



//    public void testInsertReadProvider() {
//        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
//
//        // Register a content observer for our insert.  This time, directly with the content resolver
//        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(LocationEntry.CONTENT_URI, true, tco);
//        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);
//
//        // Did our content observer get called?  Students:  If this fails, your insert location
//        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);
//
//        long locationRowId = ContentUris.parseId(locationUri);
//
//        // Verify we got a row back.
//        assertTrue(locationRowId != -1);
//
//        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
//        // the round trip.
//
//        // A cursor is your primary interface to the query results.
//        Cursor cursor = mContext.getContentResolver().query(
//                LocationEntry.CONTENT_URI,
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null  // sort order
//        );
//
//        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
//                cursor, testValues);
//
//        // Fantastic.  Now that we have a location, add some weather!
//        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
//        // The TestContentObserver is a one-shot class
//        tco = TestUtilities.getTestContentObserver();
//
//        mContext.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, tco);
//
//        Uri weatherInsertUri = mContext.getContentResolver()
//                .insert(WeatherEntry.CONTENT_URI, weatherValues);
//        assertTrue(weatherInsertUri != null);
//
//        // Did our content observer get called?  Students:  If this fails, your insert weather
//        // in your ContentProvider isn't calling
//        // getContext().getContentResolver().notifyChange(uri, null);
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);
//
//        // A cursor is your primary interface to the query results.
//        Cursor weatherCursor = mContext.getContentResolver().query(
//                WeatherEntry.CONTENT_URI,  // Table to Query
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null // columns to group by
//        );
//
//        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
//                weatherCursor, weatherValues);
//
//        // Add the location values in with the weather data so that we can make
//        // sure that the join worked and we actually get all the values back
//        weatherValues.putAll(testValues);
//
//        // Get the joined Weather and Location data
//        weatherCursor = mContext.getContentResolver().query(
//                WeatherEntry.buildWeatherLocation(TestUtilities.TEST_LOCATION),
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null  // sort order
//        );
//        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data.",
//                weatherCursor, weatherValues);
//
//        // Get the joined Weather and Location data with a start date
//        weatherCursor = mContext.getContentResolver().query(
//                WeatherEntry.buildWeatherLocationWithStartDate(
//                        TestUtilities.TEST_LOCATION, TestUtilities.TEST_DATE),
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null  // sort order
//        );
//        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data with start date.",
//                weatherCursor, weatherValues);
//
//        // Get the joined Weather data for a specific date
//        weatherCursor = mContext.getContentResolver().query(
//                WeatherEntry.buildWeatherLocationWithDate(TestUtilities.TEST_LOCATION, TestUtilities.TEST_DATE),
//                null,
//                null,
//                null,
//                null
//        );
//        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location data for a specific date.",
//                weatherCursor, weatherValues);
//    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver videosObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PopularMovieContract.VideosEntry.CONTENT_URI, true, videosObserver);

        // Register a content observer for our weather delete.
        TestUtilities.TestContentObserver popularMovieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PopularMovieContract.PopularMovieEntry.CONTENT_URI, true, popularMovieObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        videosObserver.waitForNotificationOrFail();
        popularMovieObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(videosObserver);
        mContext.getContentResolver().unregisterContentObserver(popularMovieObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertWeatherValues() {
        long currentTestDate = TestUtilities.TEST_DATE;
        long millisecondsInADay = 1000*60*60*24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentTestDate+= millisecondsInADay ) {
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(PopularMovieContract.PopularMovieEntry._ID, i);
            weatherValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_ORIGINAL_TITLE, "The Big-Hearted Will Take Away the Bride");
            weatherValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_POSTER_MAP, "\\/uC6TTUhPpQCmgldGyYveKRAu8JN.jpg");
            weatherValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_OVERWIEW, "Raj is a rich, carefree, happy-go-lucky second generation NRI. Simran ..." +
                    " in the medieval world. ");
            weatherValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_VOTE_AVERAGE, 9.2);
            weatherValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_RELEASE_DATE, "1995-10-20");

            returnContentValues[i] = weatherValues;
        }
        return returnContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createPopularMoviesValues(TEST_POPULAR_MOVIE_ID);
        Uri locationUri = mContext.getContentResolver().insert(PopularMovieContract.PopularMovieEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertWeatherValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PopularMovieContract.PopularMovieEntry.CONTENT_URI, true, weatherObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(PopularMovieContract.PopularMovieEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        weatherObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
