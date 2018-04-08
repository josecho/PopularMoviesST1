package com.udacity.course.popularmoviesst1.app;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.udacity.course.popularmoviesst1.app.adapter.VideoAdapter;
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

/**
 * Created by josecho on 4/8/18.
 */

public class FetchVideosTask extends AsyncTask<Integer, Void, List<Video>> {

    private static final String GET = "GET";
    private static final String VIDEOS_STRING = "VIDEOS string: ";
    private static final String ERROR = "Error ";
    private static final String ERROR_CLOSING_STREAM = "Error closing stream";
    private static final String VIDEO_PLATFORM = "YouTube";
    private final String LOG_TAG = FetchMoviePosterTask.class.getSimpleName();

    private VideoAdapter videoAdapter;
    private final Context context;
    CardView cardviewVideos;;


    public FetchVideosTask(Context mContext,VideoAdapter videoAdapter,CardView cardviewVideos) {
        this.videoAdapter = videoAdapter;
        this.context = mContext;
        this.cardviewVideos=cardviewVideos;
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
        JSONArray videoJsonArray = videoJson.optJSONArray(TAG_RESULTS);
        List<Video> videos = new ArrayList<Video>();
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
            }
        }
        return videos;
    }


    @Override
    protected void onPostExecute(List<Video> result) {
        if (result != null) {
            if (result.size() > 0) {
                cardviewVideos.setVisibility(View.VISIBLE);
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
