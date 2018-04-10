package com.udacity.course.popularmoviesst1.app;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.udacity.course.popularmoviesst1.app.adapter.ReviewAdapter;
import com.udacity.course.popularmoviesst1.app.model.Review;

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
 * Created by josecho on 4/10/18.
 */

class FetchReviewsTask extends AsyncTask<Integer, Void, List<Review>>{

    private static final String GET = "GET";
    private static final String VIDEOS_STRING = "REVIEWS string: ";
    private static final String ERROR = "Error ";
    private static final String ERROR_CLOSING_STREAM = "Error closing stream";
    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    private final Context mContext;
    private final ReviewAdapter reviewAdapter;

    public FetchReviewsTask(Context context, ReviewAdapter reviewAdapter) {
        this.mContext = context;
        this.reviewAdapter = reviewAdapter;
    }

    @Override
    protected List<Review> doInBackground(Integer... params) {
        if (params.length == 0) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviesJsonStr;
        Integer idReview = params[0];
        try {
            final String VIDEOS_BASE_URL = "http://api.themoviedb.org/3/movie/" + idReview + "/reviews";
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
            reviesJsonStr = stringBuilder.toString();
            Log.v(LOG_TAG, VIDEOS_STRING + reviesJsonStr);


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
            return getReviewsDataFromJson(reviesJsonStr);
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
    private List<Review> getReviewsDataFromJson(String reviesJsonStr)
            throws JSONException {

        final String TAG_RESULTS = "results";
        final String TAG_REVIEW_ID = "id";
        final String TAG_AUTOR = "author";
        final String TAG_CONTENT = "content";
        final String TAG_URL = "url";

        JSONObject reviewJson = new JSONObject(reviesJsonStr);
        JSONArray reviewJsonArray = reviewJson.optJSONArray(TAG_RESULTS);
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < reviewJsonArray.length(); i++) {
            Review review = new Review();
            JSONObject reviewInfo = reviewJsonArray.optJSONObject(i);
                review.setId(reviewInfo.getString(TAG_REVIEW_ID));
                review.setAuthor(reviewInfo.getString(TAG_AUTOR));
                review.setContent(reviewInfo.getString(TAG_CONTENT));
                review.setUrl(reviewInfo.getString(TAG_URL));
                reviews.add(review);
        }
        return reviews;
    }


    @Override
    protected void onPostExecute(List<Review> result) {
        if (result != null) {
            if (result.size() > 0) {
                View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
                CardView cardViewReview = rootView.findViewById(R.id.detail_review);
                cardViewReview.setVisibility(View.VISIBLE);
                if (reviewAdapter != null) {
                    reviewAdapter.clear();
                    for (Review review : result) {
                        reviewAdapter.add(review);
                    }
                }
            }
        }
    }




}
