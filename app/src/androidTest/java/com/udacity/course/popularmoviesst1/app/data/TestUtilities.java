package com.udacity.course.popularmoviesst1.app.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.udacity.course.popularmoviesst1.app.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {

    static final Integer TEST_POPULAR_MOVIE_ID = 269149;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createPopularMoviesValues() {
        ContentValues popularMoviesValues = new ContentValues();
        popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_POPULAR_MOVIE_ID, TEST_POPULAR_MOVIE_ID);
        popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_ORIGINAL_TITLE, "The Big-Hearted Will Take Away the Bride");
        popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_POSTER_MAP, "\\/uC6TTUhPpQCmgldGyYveKRAu8JN.jpg");
        popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_OVERWIEW, "Raj is a rich, carefree, happy-go-lucky second generation NRI. Simran ..." +
                " in the medieval world. ");
        popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_VOTE_AVERAGE, 9.2);
        popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_RELEASE_DATE, "1995-10-20");
        return popularMoviesValues;
    }

    static ContentValues createVideosValues(Integer column_popular_movie_id) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(PopularMovieContract.VideosEntry.COLUMN_VIDEO_ID, "571cb2c0c3a36843150006ed");
        testValues.put(PopularMovieContract.VideosEntry.COLUMN_POPULAR_MOVIE_ID, column_popular_movie_id);
        testValues.put(PopularMovieContract.VideosEntry.COLUMN_ISO_639_1, "en");
        testValues.put(PopularMovieContract.VideosEntry.COLUMN_ISO_3166_1, "US");
        testValues.put(PopularMovieContract.VideosEntry.COLUMN_KEY, "zQ2XkyDTW34");
        testValues.put(PopularMovieContract.VideosEntry.COLUMN_NAME, "Have a Donut Clip - Zootopia");
        testValues.put(PopularMovieContract.VideosEntry.COLUMN_SITE, "YouTube");
        testValues.put(PopularMovieContract.VideosEntry.COLUMN_SIZE, 1080);
        testValues.put(PopularMovieContract.VideosEntry.COLUMN_TYPE, "Clip");
        return testValues;
    }


    static ContentValues createReviewsValues(Integer column_popular_movie_id) {
        ContentValues testValues = new ContentValues();
        testValues.put(PopularMovieContract.ReviewsEntry.COLUMN_REVIEW_ID, "56e4290b92514172c7001002");
        testValues.put(PopularMovieContract.ReviewsEntry.COLUMN_POPULAR_MOVIE_ID, column_popular_movie_id);
        testValues.put(PopularMovieContract.ReviewsEntry.COLUMN_AUTHOR, "Andres Gomez");
        testValues.put(PopularMovieContract.ReviewsEntry.COLUMN_CONTENT, "One of the best movies Disney has created in the last years. Smart plot with a" +
                " great background topic talking about the differences, stereotypes, prejudices and joining the tendency of giving women more important" +
                " roles.It has still several gaps to fill and enhance on the latest point but it is, IMHO, a milestone in the right" +
                " direction.The characters work pretty well and it is funny when needed and not too full of cheesy songs.");
        testValues.put(PopularMovieContract.ReviewsEntry.COLUMN_URL, "https://www.themoviedb.org/review/56e4290b92514172c7001002");
        return testValues;
    }


    /*
        The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
