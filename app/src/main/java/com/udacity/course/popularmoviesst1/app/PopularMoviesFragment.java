package com.udacity.course.popularmoviesst1.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
 * Encapsulates fetching the MoviePoster and displaying it as a {@link GridView} layout.
 */
public class PopularMoviesFragment extends Fragment {

    private static final String INTERNET_CONNECTION_NOT_PRESENT = "Internet Connection Not Present";
    private final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();
    private ImageAdapter imageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.popularmoviesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updatePopularMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        imageAdapter = new ImageAdapter(getActivity(),new MoviePoster[]{});

        GridView mGridView = rootView.findViewById(R.id.movies_gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoviePoster moviePoster = (MoviePoster) imageAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(getResources().getString(R.string.movie_poster), moviePoster);
                startActivity(intent);
            }
        });
        mGridView.setAdapter(imageAdapter);
        
        return rootView;
    }


    private void updatePopularMovies() {
        if (isNetworkAvailable()) {
            FetchMoviePosterTask fetchMoviePosterTask = new FetchMoviePosterTask();
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());

            String orderBy = sharedPrefs.getString(
                    getString(R.string.pref_orders_key),
                    getString(R.string.pref_order_default));

            Log.e(LOG_TAG, orderBy);
            Log.e(LOG_TAG, orderBy);
            Log.e(LOG_TAG, orderBy);
            fetchMoviePosterTask.execute(orderBy);



        }else{
            Toast.makeText(getActivity(), INTERNET_CONNECTION_NOT_PRESENT, Toast.LENGTH_LONG).show();
        }
    }

    //https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (NullPointerException nullPointer) {
            Log.e(LOG_TAG, INTERNET_CONNECTION_NOT_PRESENT, nullPointer);
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePopularMovies();
    }

    public class FetchMoviePosterTask extends AsyncTask<String, Void, MoviePoster[]> {

        private static final String GET = "GET";
        private static final String MOVIE_POSTER_STRING = "MoviePoster string: ";
        private static final String ERROR = "Error ";
        private static final String ERROR_CLOSING_STREAM = "Error closing stream";
        private final String LOG_TAG = FetchMoviePosterTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private MoviePoster[] getWeatherDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TAG_RESULTS = "results";
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
                moviePosters[i].setOriginalTitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
                moviePosters[i].setPosterPath(movieInfo.getString(TAG_POSTER_PATH));
                moviePosters[i].setOverview(movieInfo.getString(TAG_OVERVIEW));
                moviePosters[i].setVoteAverage(movieInfo.getDouble(TAG_VOTE_AVERAGE));
                moviePosters[i].setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));
            }
            return moviePosters;
        }

        @Override
        protected MoviePoster[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviePosterJsonStr;
            try {
                final String MOVIE_POSTER_BASE_URL = "https://api.themoviedb.org/3/movie/";
                final String QUESTION_MARK = "?";
                final String API_KEY_PARAM = "api_key";
                final String URL = MOVIE_POSTER_BASE_URL +  params[0] + QUESTION_MARK;
                Uri builtUri = Uri.parse(URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.OPEN_MOVIE_POSTER_API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.e(LOG_TAG, url.toString());
                Log.e(LOG_TAG, url.toString());
                Log.e(LOG_TAG, url.toString());
                Log.e(LOG_TAG, url.toString());
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
                return getWeatherDataFromJson(moviePosterJsonStr);
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
}
