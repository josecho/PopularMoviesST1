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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    // Since we want each test to start with a clean slate
    private void deleteTheDatabase() {
        mContext.deleteDatabase(PopularMovieDbHelper.DATABASE_NAME);
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(PopularMovieContract.PopularMovieEntry.TABLE_NAME);
        tableNameHashSet.add(PopularMovieContract.VideosEntry.TABLE_NAME);
        tableNameHashSet.add(PopularMovieContract.ReviewsEntry.TABLE_NAME);

        mContext.deleteDatabase(PopularMovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new PopularMovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain 3 tables
        assertTrue("Error: Your database was created without 3 entry tables",
                tableNameHashSet.isEmpty());
        popularMovieContainsCorrectColumns(db);
        videosContainsCorrectColumns(db);
        reviewsContainsCorrectColumns(db);
        db.close();
    }

    private void popularMovieContainsCorrectColumns(SQLiteDatabase db) {
        Cursor c;// now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + PopularMovieContract.PopularMovieEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> popularMovieColumnHashSet = new HashSet<>();
        //popularMovieColumnHashSet.add(PopularMovieContract.PopularMovieEntry._ID);
        popularMovieColumnHashSet.add(PopularMovieContract.PopularMovieEntry._ID);
        popularMovieColumnHashSet.add(PopularMovieContract.PopularMovieEntry.COLUMN_ORIGINAL_TITLE);
        popularMovieColumnHashSet.add(PopularMovieContract.PopularMovieEntry.COLUMN_POSTER_MAP);
        popularMovieColumnHashSet.add(PopularMovieContract.PopularMovieEntry.COLUMN_OVERWIEW);
        popularMovieColumnHashSet.add(PopularMovieContract.PopularMovieEntry.COLUMN_VOTE_AVERAGE);
        popularMovieColumnHashSet.add(PopularMovieContract.PopularMovieEntry.COLUMN_RELEASE_DATE);
        popularMovieColumnHashSet.add(PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE);
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            popularMovieColumnHashSet.remove(columnName);
        } while(c.moveToNext());
        // if this fails, it means that your database doesn't contain all of the required
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required popular movies entry columns",
                popularMovieColumnHashSet.isEmpty());
    }

    private void videosContainsCorrectColumns(SQLiteDatabase db) {
        Cursor c;
        c = db.rawQuery("PRAGMA table_info(" + PopularMovieContract.VideosEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> videosColumnHashSet = new HashSet<>();
        videosColumnHashSet.add(PopularMovieContract.VideosEntry.COLUMN_POPULAR_MOVIE_ID);
        videosColumnHashSet.add(PopularMovieContract.VideosEntry.COLUMN_ISO_639_1);
        videosColumnHashSet.add(PopularMovieContract.VideosEntry.COLUMN_ISO_3166_1);
        videosColumnHashSet.add(PopularMovieContract.VideosEntry.COLUMN_KEY);
        videosColumnHashSet.add(PopularMovieContract.VideosEntry.COLUMN_NAME);
        videosColumnHashSet.add(PopularMovieContract.VideosEntry.COLUMN_SITE);
        videosColumnHashSet.add(PopularMovieContract.VideosEntry.COLUMN_SIZE);
        videosColumnHashSet.add(PopularMovieContract.VideosEntry.COLUMN_TYPE);
        videosColumnHashSet.add(PopularMovieContract.VideosEntry.COLUMN_FAVORITE);
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            videosColumnHashSet.remove(columnName);
        } while(c.moveToNext());
        // if this fails, it means that your database doesn't contain all of the required
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required videos entry columns",
                videosColumnHashSet.isEmpty());
    }

    private void reviewsContainsCorrectColumns(SQLiteDatabase db) {
        Cursor c;
        c = db.rawQuery("PRAGMA table_info(" + PopularMovieContract.ReviewsEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> reviewsColumnHashSet = new HashSet<>();
        reviewsColumnHashSet.add(PopularMovieContract.ReviewsEntry._ID);
        reviewsColumnHashSet.add(PopularMovieContract.ReviewsEntry.COLUMN_POPULAR_MOVIE_ID);
        reviewsColumnHashSet.add(PopularMovieContract.ReviewsEntry.COLUMN_AUTHOR);
        reviewsColumnHashSet.add(PopularMovieContract.ReviewsEntry.COLUMN_CONTENT);
        reviewsColumnHashSet.add(PopularMovieContract.ReviewsEntry.COLUMN_URL);
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            reviewsColumnHashSet.remove(columnName);
        } while(c.moveToNext());
        // if this fails, it means that your database doesn't contain all of the required
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required reviews entry columns",
                reviewsColumnHashSet.isEmpty());
    }

    public void testPopularMovieTable() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createPopularMoviesValues(TestUtilities.TEST_POPULAR_MOVIE_ID);

        // Third Step: Insert ContentValues into database and get a row ID back
        long popularMovieRowId;
        popularMovieRowId = db.insert(PopularMovieContract.PopularMovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(popularMovieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                PopularMovieContract.PopularMovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
    }

    public void testVideosTable() {
        insertVideos(TestUtilities.TEST_VIDEO_ID,TestUtilities.TEST_POPULAR_MOVIE_ID);
    }

    public void testReviewsTable() {
        insertReviews(TestUtilities.TEST_POPULAR_MOVIE_ID);
    }

    public void testAllTables() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Weather): Create weather values
        ContentValues popularMoviesValues = TestUtilities.createPopularMoviesValues(TestUtilities.TEST_POPULAR_MOVIE_ID);

        // Third Step (Weather): Insert ContentValues into database and get a row ID back
        long weatherRowId = db.insert(PopularMovieContract.PopularMovieEntry.TABLE_NAME, null, popularMoviesValues);
        assertTrue(weatherRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor popularMovieCursor = db.query(
                PopularMovieContract.PopularMovieEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from PopularMovieEntry query", popularMovieCursor.moveToFirst() );

        Integer column_popular_movie_id=0;
        if (popularMovieCursor.moveToFirst()){
            column_popular_movie_id = popularMovieCursor.getInt(popularMovieCursor.getColumnIndex(PopularMovieContract.PopularMovieEntry._ID));
        }
        assertEquals(TestUtilities.TEST_POPULAR_MOVIE_ID,column_popular_movie_id);

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb PopularMovieEntry failed to validate",
                popularMovieCursor, popularMoviesValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                popularMovieCursor.moveToNext() );


        long videoRowId = insertVideos(TestUtilities.TEST_VIDEO_ID,column_popular_movie_id);
        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", videoRowId == -1L);

        long reviewRowId = insertReviews(column_popular_movie_id);
        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", reviewRowId == -1L);

        // Sixth Step: Close cursor and database
        popularMovieCursor.close();
        dbHelper.close();
    }

    private long insertVideos(String videoId,Integer column_popular_movie_id) {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testVideosValues = TestUtilities.createVideosValues(videoId,column_popular_movie_id);
        // Third Step: Insert ContentValues into database and get a row ID back
        long videoRowId;
        videoRowId = db.insert(PopularMovieContract.VideosEntry.TABLE_NAME, null, testVideosValues);
        // Verify we got a row back.
        assertTrue(videoRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                PopularMovieContract.VideosEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from videos query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Videos Query Validation Failed",
                cursor, testVideosValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from videos query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return videoRowId;
    }

    private long insertReviews(Integer column_popular_movie_id) {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testReviewsValues = TestUtilities.createReviewsValues(column_popular_movie_id);
        // Third Step: Insert ContentValues into database and get a row ID back
        long reviewRowId;
        reviewRowId = db.insert(PopularMovieContract.ReviewsEntry.TABLE_NAME, null, testReviewsValues);
        // Verify we got a row back.
        assertTrue(reviewRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                PopularMovieContract.ReviewsEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from Reviews query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Reviews Query Validation Failed",
                cursor, testReviewsValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from Reviews query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return reviewRowId;
    }


}
