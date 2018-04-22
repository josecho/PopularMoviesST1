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
package com.udacity.course.popularmoviesst1.app;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.udacity.course.popularmoviesst1.app.data.PopularMovieContract;

public class TestFetchPopularMoviesTask extends AndroidTestCase{

    static final String ADD_VIDEO_ID = "571cb2c0c3a36843150006ed";
    static final Integer ADD_POPULAR_MOVIE_ID = 269149;
    static final String ADD_ISO_639_1 = "en";
    static final String ADD_ISO_3166_1 = "US";
    static final String ADD_KEY = "zQ2XkyDTW34";
    static final String ADD_NAME = "Have a Donut Clip - Zootopia";
    static final String ADD_SITE = "YouTube";
    static final Integer ADD_SIZE = 1080;
    static final String ADD_TYPE = "clip";
    static final Integer ADD_FAVORITE = 1;


    /*
        This test will only run on API level 11 and higher because of a requirement in the
        content provider.
     */
    @TargetApi(11)
    public void testAddLocation() {
        // start from a clean state
        getContext().getContentResolver().delete(PopularMovieContract.VideosEntry.CONTENT_URI,
                PopularMovieContract.VideosEntry._ID + " = ?",
                new String[]{ADD_VIDEO_ID});

        FetchMoviePosterTask fetchMoviePosterTask= new FetchMoviePosterTask(getContext(), null);
        long videoIndex = fetchMoviePosterTask.addVideo(ADD_VIDEO_ID,ADD_POPULAR_MOVIE_ID,ADD_ISO_639_1,
                ADD_ISO_3166_1,ADD_KEY,ADD_NAME,ADD_SITE,ADD_SIZE,ADD_TYPE,ADD_FAVORITE);

        // does addLocation return a valid record ID?
        assertFalse("Error: addLocation returned an invalid ID on insert",
                videoIndex == -1);

        // test all this twice
        for ( int i = 0; i < 2; i++ ) {

            // does the ID point to our location?
            Cursor videoCursor = getContext().getContentResolver().query(
                    PopularMovieContract.VideosEntry.CONTENT_URI,
                    new String[]{
                            PopularMovieContract.VideosEntry._ID,
                            PopularMovieContract.VideosEntry._ID,
                            PopularMovieContract.VideosEntry.COLUMN_POPULAR_MOVIE_ID,
                            PopularMovieContract.VideosEntry.COLUMN_ISO_639_1,
                            PopularMovieContract.VideosEntry.COLUMN_ISO_3166_1,
                            PopularMovieContract.VideosEntry.COLUMN_KEY,
                            PopularMovieContract.VideosEntry.COLUMN_NAME,
                            PopularMovieContract.VideosEntry.COLUMN_SITE,
                            PopularMovieContract.VideosEntry.COLUMN_SIZE,
                            PopularMovieContract.VideosEntry.COLUMN_TYPE,
                            PopularMovieContract.VideosEntry.COLUMN_FAVORITE,
                    },
                    PopularMovieContract.VideosEntry._ID + " = ?",
                    new String[]{ADD_VIDEO_ID},
                    null);

            // these match the indices of the projection
            if (videoCursor.moveToFirst()) {
                assertEquals("Error: the queried value of videoIndex does not match the returned value" +
                        "from addLocation", videoCursor.getLong(0), videoIndex);
                assertEquals("Error: the queried value of ADD_VIDEO_ID setting is incorrect",
                        videoCursor.getString(1), ADD_VIDEO_ID);
                assertEquals("Error: the queried value of location ADD_POPULAR_MOVIE_ID is incorrect",
                        videoCursor.getInt(2), 269149);
                assertEquals("Error: the queried value of ADD_ISO_639_1 is incorrect",
                        videoCursor.getString(3), ADD_ISO_639_1);
                assertEquals("Error: the queried value of ADD_ISO_3166_1 is incorrect",
                        videoCursor.getString(4), ADD_ISO_3166_1);

                assertEquals("Error: the queried value of ADD_KEY is incorrect",
                        videoCursor.getString(5), ADD_KEY);
                assertEquals("Error: the queried value of ADD_NAME is incorrect",
                        videoCursor.getString(6), ADD_NAME);
                assertEquals("Error: the queried value of ADD_SITE is incorrect",
                        videoCursor.getString(7), ADD_SITE);
                assertEquals("Error: the queried value of ADD_SIZE is incorrect",
                        videoCursor.getInt(8), 1080);
                assertEquals("Error: the queried value of ADD_TYPE is incorrect",
                        videoCursor.getString(9), ADD_TYPE);
                assertEquals("Error: the queried value of ADD_FAVORITE is incorrect",
                        videoCursor.getInt(10), 1);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }


            // there should be no more records
            assertFalse("Error: there should be only one record returned from a location query",
                    videoCursor.moveToNext());

            // add the location again
           /* long newVideoIndex = fetchMoviePosterTask.addVideo(ADD_VIDEO_ID,ADD_POPULAR_MOVIE_ID, ADD_ISO_639_1, ADD_ISO_3166_1,
                    ADD_KEY, ADD_NAME,ADD_SITE,ADD_SIZE,ADD_TYPE,  ADD_FAVORITE);

            assertEquals("Error: inserting a location again should return the same ID",
                    videoIndex, newVideoIndex);*/
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(PopularMovieContract.VideosEntry.CONTENT_URI,
                PopularMovieContract.VideosEntry._ID + " = ?",
                new String[]{ADD_VIDEO_ID});

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(PopularMovieContract.VideosEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }
}
