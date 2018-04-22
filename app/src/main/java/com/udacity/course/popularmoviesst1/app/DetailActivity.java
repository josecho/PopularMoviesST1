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

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;
import com.udacity.course.popularmoviesst1.app.adapter.ReviewAdapter;
import com.udacity.course.popularmoviesst1.app.adapter.VideoAdapter;
import com.udacity.course.popularmoviesst1.app.data.PopularMovieContract;
import com.udacity.course.popularmoviesst1.app.model.MoviePoster;
import com.udacity.course.popularmoviesst1.app.model.Review;
import com.udacity.course.popularmoviesst1.app.model.Video;

import java.util.ArrayList;

import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;

public class DetailActivity extends AppCompatActivity {

    private static final String INTERNET_CONNECTION_NOT_PRESENT = "Internet Connection Not Present";

    ScrollView mScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        mScrollView = (ScrollView) findViewById(R.id.nc_view);
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScrollView = (ScrollView) findViewById(R.id.nc_view);
        if (savedInstanceState != null) {
            final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
            if (position != null)
                mScrollView.post(new Runnable() {
                    public void run() {
                        mScrollView.scrollTo(position[0], position[1]);
                    }
                });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String MOVIE_POSTER_SHARE_HASHTAG = " #MoviePosterApp";

        MoviePoster moviePoster;

        private LinearListView linListViewVideos;
        private VideoAdapter videoAdapter;
        private CardView cardViewVideos;

        private LinearListView linListViewReviews;
        private ReviewAdapter reviewAdapter;
        private CardView cardViewReviews;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }


        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            // The detail Activity called via intent.  Inspect the intent for movie poster data.
            Intent intent = getActivity().getIntent();

            if (intent != null && intent.hasExtra(getResources().getString(R.string.movie_poster))) {

                TextView tvOriginalTitle = rootView.findViewById(R.id.tv_original_title);
                ImageView ivMoviePoster = rootView.findViewById(R.id.iv_movie_poster);
                TextView tvReleaseDate = rootView.findViewById(R.id.tv_release_date);
                TextView tvOverView = rootView.findViewById(R.id.tv_Over_View);
                TextView tvVoteAverage = rootView.findViewById(R.id.tv_Vote_Average);

                Button videoButton = rootView.findViewById(R.id.favorite_button);
                moviePoster = intent.getParcelableExtra(getString(R.string.movie_poster));
                videoButton.setOnClickListener(mVideoButtonOnClickListener);

                Cursor cursor = getActivity().getContentResolver().query(
                        PopularMovieContract.PopularMovieEntry.CONTENT_URI,
                        null,   // projection
                        PopularMovieContract.PopularMovieEntry._ID + " = " + moviePoster.getMoviePosterId(),
                        null,   // Values for the "where" clause
                        null    // sort order
                );
                if(cursor.moveToFirst()){
                    String favorite = cursor.getString(cursor.getColumnIndex(PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE));
                    moviePoster.setFavorite(Integer.valueOf(favorite));
                    adaptVideoButton(videoButton);
                }

                if((moviePoster.getOriginalTitle().isEmpty())){
                    tvOriginalTitle.setText(R.string.no_title_found);
                }else{
                    tvOriginalTitle.setText(moviePoster.getOriginalTitle());
                }
                //ICONS: https://material.io/guidelines/resources/sticker-sheets-icons.html#sticker-sheets-icons-system-icons
                Picasso.with(getActivity())
                        .load(moviePoster.getPosterPath())
                        .error(R.drawable.ic_error_black_24dp)
                        .placeholder(R.drawable.ic_search_black_24dp)
                        .into(ivMoviePoster);
                if((moviePoster.getReleaseDate().isEmpty())){
                    tvReleaseDate.setText(R.string.no_title_found);
                }else{
                    tvReleaseDate.setText(moviePoster.getReleaseDate().substring(0,4));
                }
                if (moviePoster.getOverview().isEmpty()) {
                    tvOverView.setText(getResources().getString(R.string.no_synopsis_found));
                }else{
                    tvOverView.setText(moviePoster.getOverview());
                }
                if (moviePoster.getVoteAverage().isEmpty()) {
                    tvVoteAverage.setText(getResources().getString(R.string.no_average_found));
                }else{
                    tvVoteAverage.setText(moviePoster.getVoteAverage());
                }

                linListViewVideos = rootView.findViewById(R.id.detail_videos);
                cardViewVideos = rootView.findViewById(R.id.detail_video_youtube);
                videoAdapter = new VideoAdapter(getActivity(), new ArrayList<Video>());
                linListViewVideos.setAdapter(videoAdapter);

                linListViewReviews = rootView.findViewById(R.id.detail_reviews);
                cardViewReviews = rootView.findViewById(R.id.detail_review);
                reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());
                linListViewReviews.setAdapter(reviewAdapter);
            }
            return rootView;
        }

        private final View.OnClickListener mVideoButtonOnClickListener = new View.OnClickListener() {
            public void onClick(View view) {

                Button videoButton = view.findViewById(R.id.favorite_button);

                setVideoButton(videoButton);

                Log.d(LOG_TAG, "ActivityNotFoundException. Could not find activity to handle ");

            }
        };

        private void adaptVideoButton(Button videoButton) {
            if(moviePoster.getFavorite()==1) {
                videoButton.setTextColor(YELLOW);
                videoButton.setText("Is favorite");
                ContentValues updatedValues = new ContentValues();
                updatedValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE, 1);
                Integer id = moviePoster.getMoviePosterId();
                int count = getActivity().getContentResolver().update(
                        PopularMovieContract.PopularMovieEntry.CONTENT_URI, updatedValues, PopularMovieContract.PopularMovieEntry._ID + "= ?",
                        new String[]{Long.toString(id)});
                Log.d(LOG_TAG, "update:  " + count);
                moviePoster.setFavorite(1);

            }else{
                videoButton.setTextColor(RED);
                videoButton.setText("MARK AS FAVORITE");
                ContentValues updatedValues = new ContentValues();
                //updatedValues.put(PopularMovieContract.VideosEntry._ID, vidoeosRowId);
                updatedValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE, 0);
                Integer id = moviePoster.getMoviePosterId();
                int count = getActivity().getContentResolver().update(
                        PopularMovieContract.PopularMovieEntry.CONTENT_URI, updatedValues, PopularMovieContract.PopularMovieEntry._ID + "= ?",
                        new String[]{Long.toString(id)});
                Log.d(LOG_TAG, "update:  " + count);
                moviePoster.setFavorite(0);
            }
        }

        private void setVideoButton(Button videoButton) {
            if(moviePoster.getFavorite()==0) {
                videoButton.setTextColor(YELLOW);
                videoButton.setText("Is favorite");
                ContentValues updatedValues = new ContentValues();
                updatedValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE, 1);
                Integer id = moviePoster.getMoviePosterId();
                int count = getActivity().getContentResolver().update(
                        PopularMovieContract.PopularMovieEntry.CONTENT_URI, updatedValues, PopularMovieContract.PopularMovieEntry._ID + "= ?",
                        new String[]{Long.toString(id)});
                Log.d(LOG_TAG, "update:  " + count);
                moviePoster.setFavorite(1);

            }else{
                videoButton.setTextColor(RED);
                videoButton.setText("MARK AS FAVORITE");
                ContentValues updatedValues = new ContentValues();
                //updatedValues.put(PopularMovieContract.VideosEntry._ID, vidoeosRowId);
                updatedValues.put(PopularMovieContract.PopularMovieEntry.COLUMN_FAVORITE, 0);
                Integer id = moviePoster.getMoviePosterId();
                int count = getActivity().getContentResolver().update(
                        PopularMovieContract.PopularMovieEntry.CONTENT_URI, updatedValues, PopularMovieContract.PopularMovieEntry._ID + "= ?",
                        new String[]{Long.toString(id)});
                Log.d(LOG_TAG, "update:  " + count);
                moviePoster.setFavorite(0);
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareMoviePosterIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }

        private Intent createShareMoviePosterIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    moviePoster.toString() + MOVIE_POSTER_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onStart() {
            super.onStart();
            updateVideos();
        }

        private void updateVideos() {
            if (isNetworkAvailable()) {
                FetchVideosTask fetchVideosTask = new FetchVideosTask(getActivity(),videoAdapter);
                fetchVideosTask.execute(moviePoster.getMoviePosterId());
                FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity(),reviewAdapter);
                fetchReviewsTask.execute(moviePoster.getMoviePosterId());

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

    }
}
