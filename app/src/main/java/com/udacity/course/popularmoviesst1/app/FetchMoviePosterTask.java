package com.udacity.course.popularmoviesst1.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.udacity.course.popularmoviesst1.app.adapter.ImageAdapter;
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

    public FetchMoviePosterTask(ImageAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
    }

    @Override
    protected MoviePoster[] doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviePosterJsonStr;
        String orderBy = params[0];
        try {
            final String MOVIE_POSTER_BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String QUESTION_MARK = "?";
            final String API_KEY_PARAM = "api_key";
            final String URL = MOVIE_POSTER_BASE_URL +  orderBy + QUESTION_MARK;
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
        return null;
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


        JSONObject moviePosterJson = new JSONObject(moviesJsonStr);
        JSONArray moviePosterJsonArray = moviePosterJson.optJSONArray(TAG_RESULTS);
        MoviePoster[] moviePosters = new MoviePoster[moviePosterJsonArray.length()];

        for (int i = 0; i < moviePosterJsonArray.length(); i++) {
            moviePosters[i] = new MoviePoster();
            JSONObject movieInfo = moviePosterJsonArray.optJSONObject(i);
            moviePosters[i].setMoviePosterId(movieInfo.getInt(TAG_MOVIE_ID));
            moviePosters[i].setOriginalTitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
            moviePosters[i].setPosterPath(movieInfo.getString(TAG_POSTER_PATH));
            moviePosters[i].setOverview(movieInfo.getString(TAG_OVERVIEW));
            moviePosters[i].setVoteAverage(movieInfo.getDouble(TAG_VOTE_AVERAGE));
            moviePosters[i].setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));
        }
        return moviePosters;
    }


    @Override
    protected void onPostExecute(MoviePoster[] result) {
        if (result != null) {
            imageAdapter.setMoviePoster(result);
            imageAdapter.notifyDataSetChanged();
        }
    }
}
