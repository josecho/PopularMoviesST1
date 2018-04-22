package com.udacity.course.popularmoviesst1.app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.udacity.course.popularmoviesst1.app.adapter.ImageAdapter;
import com.udacity.course.popularmoviesst1.app.data.PopularMovieContract;
import com.udacity.course.popularmoviesst1.app.model.MoviePoster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by josecho on 4/8/18.
 */

class FetchMoviePosterTask extends AsyncTask<String, Void, MoviePoster[]> {

    private static final String GET = "GET";
    private static final String MOVIE_POSTER_STRING = "MoviePoster string: ";
    private static final String ERROR = "Error ";
    private static final String ERROR_CLOSING_STREAM = "Error closing stream";
    private final String LOG_TAG = FetchMoviePosterTask.class.getSimpleName();

    private final ImageAdapter imageAdapter;
    private Context mContext;

    public FetchMoviePosterTask(Context context,ImageAdapter imageAdapter) {
        this.mContext = context;
        this.imageAdapter = imageAdapter;
    }

    @Override
    protected MoviePoster[] doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String orderBy = params[0];
        if (orderBy.equals("favorites")){

            Cursor favoritesCursor = mContext.getContentResolver().query(
                    PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                    new String[]{PopularMovieContract.PopularMovieEntry._ID,
                            PopularMovieContract.PopularMovieEntry.COLUMN_ORIGINAL_TITLE,
                            PopularMovieContract.PopularMovieEntry.COLUMN_POSTER_MAP,
                            PopularMovieContract.PopularMovieEntry.COLUMN_OVERWIEW,
                            PopularMovieContract.PopularMovieEntry.COLUMN_VOTE_AVERAGE,
                            PopularMovieContract.PopularMovieEntry.COLUMN_RELEASE_DATE,
                            PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE
                    },
                    PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE + " = ?",
                    new String[]{String.valueOf(1)},
                    null);

            JSONArray resultSet = new JSONArray();
            favoritesCursor.moveToFirst();
            while (favoritesCursor.isAfterLast() == false) {
                int totalColumn = favoritesCursor.getColumnCount();
                JSONObject rowObject = new JSONObject();
                for (int i = 0; i < totalColumn; i++) {
                    if (favoritesCursor.getColumnName(i) != null) {
                        try {
                            rowObject.put(favoritesCursor.getColumnName(i),
                                    favoritesCursor.getString(i));
                        } catch (Exception e) {
                            Log.d(LOG_TAG, e.getMessage());
                        }
                    }
                }
                resultSet.put(rowObject);
                favoritesCursor.moveToNext();
            }

            favoritesCursor.close();
            final String TAG_MOVIE_ID = "_id";
            final String TAG_ORIGINAL_TITLE = "original_title";
            //movie poster image thumbnail
            final String TAG_POSTER_PATH = "poster_path";
            //A plot synopsis (called overview in the api)
            final String TAG_OVERVIEW = "overview";
            //user rating (called vote_average in the api)
            final String TAG_VOTE_AVERAGE = "vote_average";
            final String TAG_RELEASE_DATE = "release_date";
            final String TAG_FAVORITE = "favorite";

            MoviePoster[] moviePosters = new MoviePoster[resultSet.length()];
            for (int i = 0; i < resultSet.length(); i++) {
                moviePosters[i] = new MoviePoster();
                JSONObject movieInfo = resultSet.optJSONObject(i);
                try {
                    moviePosters[i].setMoviePosterId(movieInfo.getInt(TAG_MOVIE_ID));
                    moviePosters[i].setOriginalTitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
                    moviePosters[i].setPosterPath(movieInfo.getString(TAG_POSTER_PATH));
                    moviePosters[i].setOverview(movieInfo.getString(TAG_OVERVIEW));
                    moviePosters[i].setVoteAverage(movieInfo.getDouble(TAG_VOTE_AVERAGE));
                    moviePosters[i].setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));
                    moviePosters[i].setFavorite(Integer.valueOf(movieInfo.getString(TAG_FAVORITE)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return moviePosters;

        }else {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviePosterJsonStr;
            try {
                final String MOVIE_POSTER_BASE_URL = "https://api.themoviedb.org/3/movie/";
                final String QUESTION_MARK = "?";
                final String API_KEY_PARAM = "api_key";
                final String URL = MOVIE_POSTER_BASE_URL + orderBy + QUESTION_MARK;
                Uri builtUri = Uri.parse(URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.OPEN_MOVIE_POSTER_API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(GET);
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                if (stringBuilder.length() == 0) {
                    return null;
                }
                moviePosterJsonStr = stringBuilder.toString();
                Log.v(LOG_TAG, MOVIE_POSTER_STRING + moviePosterJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, ERROR, e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, ERROR_CLOSING_STREAM, e);
                    }
                }
            }

            try {
                return getMoviePosterDataFromJson(moviePosterJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return null;
    }

    long addVideo(String videoId, Integer popular_movieId, String addIso6391, String addIso31661, String addKey,
                  String videoName, String site, Integer addSize, String type, Integer favorite) {
        long locationId;

        // First, check if the location with this city name exists in the db
        Cursor locationCursor = mContext.getContentResolver().query(
                PopularMovieContract.VideosEntry.CONTENT_URI,
                new String[]{PopularMovieContract.VideosEntry._ID},
                PopularMovieContract.VideosEntry._ID + " = ?",
                new String[]{videoId},
                null);

        if (locationCursor.moveToFirst()) {
            int videoIndex = locationCursor.getColumnIndex(PopularMovieContract.VideosEntry._ID);
            locationId = locationCursor.getLong(videoIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues videoValues = new ContentValues();
            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            videoValues.put(PopularMovieContract.VideosEntry._ID, videoId);
            videoValues.put(PopularMovieContract.VideosEntry.COLUMN_POPULAR_MOVIE_ID, popular_movieId);
            videoValues.put(PopularMovieContract.VideosEntry.COLUMN_ISO_639_1, addIso6391);
            videoValues.put(PopularMovieContract.VideosEntry.COLUMN_ISO_3166_1, addIso31661);
            videoValues.put(PopularMovieContract.VideosEntry.COLUMN_KEY, addKey);
            videoValues.put(PopularMovieContract.VideosEntry.COLUMN_NAME, videoName);
            videoValues.put(PopularMovieContract.VideosEntry.COLUMN_SITE, site);
            videoValues.put(PopularMovieContract.VideosEntry.COLUMN_SIZE, 1080);
            videoValues.put(PopularMovieContract.VideosEntry.COLUMN_TYPE, type);
            videoValues.put(PopularMovieContract.VideosEntry.COLUMN_FAVORITE, 1);
            // Finally, insert location data into the database.
            Uri insertedUri = mContext.getContentResolver().insert(
                    PopularMovieContract.VideosEntry.CONTENT_URI,
                    videoValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            locationId = ContentUris.parseId(insertedUri);
        }

        locationCursor.close();
        // Wait, that worked?  Yes!
        return locationId;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private MoviePoster[] getMoviePosterDataFromJson(String moviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TAG_RESULTS = "results";
        final String TAG_MOVIE_ID = "id";
        final String TAG_ORIGINAL_TITLE = "original_title";
        //movie poster image thumbnail
        final String TAG_POSTER_PATH = "poster_path";
        //A plot synopsis (called overview in the api)
        final String TAG_OVERVIEW = "overview";
        //user rating (called vote_average in the api)
        final String TAG_VOTE_AVERAGE = "vote_average";
        final String TAG_RELEASE_DATE = "release_date";

        Cursor locationCursor;
        Cursor favoriteCursor;

        try {
            JSONObject moviePosterJson = new JSONObject(moviesJsonStr);
            JSONArray moviePosterJsonArray = moviePosterJson.optJSONArray(TAG_RESULTS);
            MoviePoster[] moviePosters = new MoviePoster[moviePosterJsonArray.length()];

            // Insert the new popular movies information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviePosterJsonArray.length());

            for (int i = 0; i < moviePosterJsonArray.length(); i++) {
            moviePosters[i] = new MoviePoster();
            JSONObject movieInfo = moviePosterJsonArray.optJSONObject(i);
            moviePosters[i].setMoviePosterId(movieInfo.getInt(TAG_MOVIE_ID));
            moviePosters[i].setOriginalTitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
            moviePosters[i].setPosterPath(movieInfo.getString(TAG_POSTER_PATH));
            moviePosters[i].setOverview(movieInfo.getString(TAG_OVERVIEW));
            moviePosters[i].setVoteAverage(movieInfo.getDouble(TAG_VOTE_AVERAGE));
            moviePosters[i].setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));


            locationCursor = mContext.getContentResolver().query(
                        PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                        new String[]{PopularMovieContract.PopularMovieEntry._ID},
                        PopularMovieContract.PopularMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(movieInfo.getInt(TAG_MOVIE_ID))},
                        null);

            if (!locationCursor.moveToFirst()){
                ContentValues popularMoviesValues = new ContentValues();
                popularMoviesValues.put(PopularMovieContract.PopularMovieEntry._ID,movieInfo.getInt(TAG_MOVIE_ID));
                popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_ORIGINAL_TITLE,movieInfo.getString(TAG_ORIGINAL_TITLE));
                popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_POSTER_MAP,movieInfo.getString(TAG_POSTER_PATH));
                popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_OVERWIEW,movieInfo.getString(TAG_OVERVIEW));
                popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_VOTE_AVERAGE,movieInfo.getDouble(TAG_VOTE_AVERAGE));
                popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_RELEASE_DATE,movieInfo.getString(TAG_RELEASE_DATE));
                popularMoviesValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE,0);
                cVVector.add(popularMoviesValues);
                Uri insertedUri= mContext.getContentResolver().insert(
                        PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                        popularMoviesValues
                );
                moviePosters[i].setFavorite(0);
                Log.d(LOG_TAG, "PopularMovieEntry insertedUri. " + insertedUri + " Inserted");
            }

            favoriteCursor = mContext.getContentResolver().query(
                        PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                        new String[]{PopularMovieContract.PopularMovieEntry._ID,PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE},
                        PopularMovieContract.PopularMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(movieInfo.getInt(TAG_MOVIE_ID))},
                        null);
            if (favoriteCursor.moveToFirst()){
                    Integer index = favoriteCursor.getColumnIndex(PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE);
                    Integer favoriteValue = Integer.valueOf(favoriteCursor.getString(index));
                    moviePosters[i].setFavorite(favoriteValue);
                    Log.d(LOG_TAG, "index index. " + favoriteValue + " index");
            }
        }
        return moviePosters;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(MoviePoster[] result) {
        if (result != null) {
            imageAdapter.setMoviePoster(result);
            imageAdapter.notifyDataSetChanged();
        }
    }
}
