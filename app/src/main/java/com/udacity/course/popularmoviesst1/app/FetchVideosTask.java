package com.udacity.course.popularmoviesst1.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.udacity.course.popularmoviesst1.app.adapter.VideoAdapter;
import com.udacity.course.popularmoviesst1.app.data.PopularMovieContract;
import com.udacity.course.popularmoviesst1.app.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by josecho on 4/8/18.
 */

class FetchVideosTask extends AsyncTask<Integer, Void, List<Video>> {

    private static final String GET = "GET";
    private static final String VIDEOS_STRING = "VIDEOS string: ";
    private static final String ERROR = "Error ";
    private static final String ERROR_CLOSING_STREAM = "Error closing stream";
    private static final String VIDEO_PLATFORM = "YouTube";
    private final String LOG_TAG = FetchMoviePosterTask.class.getSimpleName();

    private Context mContext;
    private VideoAdapter videoAdapter;

    public FetchVideosTask(Context context,VideoAdapter videoAdapter) {
        this.mContext = context;
        this.videoAdapter = videoAdapter;
    }

    @Override
    protected List<Video> doInBackground(Integer... params) {
        if (params.length == 0) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String videosJsonStr;
        Integer idPopularMovie = params[0];
        try {
            final String VIDEOS_BASE_URL = "https://api.themoviedb.org/3/movie/" + idPopularMovie + "/videos";
            final String QUESTION_MARK = "?";
            final String API_KEY_PARAM = "api_key";
            final String URL = VIDEOS_BASE_URL + QUESTION_MARK;
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
            videosJsonStr = stringBuilder.toString();
            Log.v(LOG_TAG, VIDEOS_STRING + videosJsonStr);

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
            return getVideosDataFromJson(videosJsonStr);
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
    private List<Video> getVideosDataFromJson(String videosJsonStr)
            throws JSONException {

        final String TAG_POPULAR_MOVIE_ID = "id";
        final String TAG_RESULTS = "results";
        final String TAG_VIDEO_ID = "id";
        final String TAG_iso_639_1 = "iso_639_1";
        final String TAG_iso_3166_1 = "iso_3166_1";
        final String TAG_KEY = "key";
        final String TAG_NAME = "name";
        final String TAG_SITE = "site";
        final String TAG_SIZE = "size";
        final String TAG_TYPE = "type";


        JSONObject videoJson = new JSONObject(videosJsonStr);
        String popularMovieId = videoJson.getString(TAG_POPULAR_MOVIE_ID);
        JSONArray videoJsonArray = videoJson.optJSONArray(TAG_RESULTS);
        List<Video> videos = new ArrayList<>();
        // Insert the new popular movies information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(videoJsonArray.length());
        for (int i = 0; i < videoJsonArray.length(); i++) {
            Video video = new Video();
            JSONObject videoInfo = videoJsonArray.optJSONObject(i);
            if(videoInfo.getString(TAG_SITE).equals(VIDEO_PLATFORM)){
                video.setId(videoInfo.getString(TAG_VIDEO_ID));
                video.setIso_639_1(videoInfo.getString(TAG_iso_639_1));
                video.setIso_3166_1(videoInfo.getString(TAG_iso_3166_1));
                video.setKey(videoInfo.getString(TAG_KEY));
                video.setName(videoInfo.getString(TAG_NAME));
                video.setSite(videoInfo.getString(TAG_SITE));
                video.setSize(videoInfo.getString(TAG_SIZE));
                video.setType(videoInfo.getString(TAG_TYPE));
                videos.add(video);

                Cursor locationCursor = mContext.getContentResolver().query(
                        PopularMovieContract.VideosEntry.CONTENT_URI,
                        new String[]{PopularMovieContract.VideosEntry._ID},
                        PopularMovieContract.VideosEntry._ID + " = ?",
                        new String[]{String.valueOf(videoInfo.getString(TAG_VIDEO_ID))},
                        null);
                if (!locationCursor.moveToFirst()) {
                    ContentValues videosValues = new ContentValues();
                    videosValues.put(PopularMovieContract.VideosEntry._ID, videoInfo.getString(TAG_VIDEO_ID));
                    videosValues.put(PopularMovieContract.VideosEntry.COLUMN_POPULAR_MOVIE_ID, popularMovieId);
                    videosValues.put(PopularMovieContract.VideosEntry.COLUMN_ISO_639_1, videoInfo.getString(TAG_iso_639_1));
                    videosValues.put(PopularMovieContract.VideosEntry.COLUMN_ISO_3166_1, videoInfo.getString(TAG_iso_3166_1));
                    videosValues.put(PopularMovieContract.VideosEntry.COLUMN_KEY, videoInfo.getString(TAG_KEY));
                    videosValues.put(PopularMovieContract.VideosEntry.COLUMN_NAME, videoInfo.getString(TAG_NAME));
                    videosValues.put(PopularMovieContract.VideosEntry.COLUMN_SITE, videoInfo.getString(TAG_SITE));
                    videosValues.put(PopularMovieContract.VideosEntry.COLUMN_SIZE, videoInfo.getString(TAG_SIZE));
                    videosValues.put(PopularMovieContract.VideosEntry.COLUMN_TYPE, videoInfo.getString(TAG_TYPE));
                    cVVector.add(videosValues);
                    Uri insertedUri= mContext.getContentResolver().insert(
                            PopularMovieContract.VideosEntry.CONTENT_URI,
                            videosValues
                    );
                }
            }

        }
        return videos;
    }


    @Override
    protected void onPostExecute(List<Video> result) {
        if (result != null) {
            if (result.size() > 0) {
                View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
                CardView cardViewReview = rootView.findViewById(R.id.detail_video_youtube);
                cardViewReview.setVisibility(View.VISIBLE);
                if (videoAdapter != null) {
                    videoAdapter.clear();
                    for (Video video : result) {
                        videoAdapter.add(video);
                    }
                }

            }
        }
    }

}
