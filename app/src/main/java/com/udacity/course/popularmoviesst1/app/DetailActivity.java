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

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;
import com.udacity.course.popularmoviesst1.app.adapter.VideoAdapter;
import com.udacity.course.popularmoviesst1.app.model.MoviePoster;
import com.udacity.course.popularmoviesst1.app.model.Video;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private static final String INTERNET_CONNECTION_NOT_PRESENT = "Internet Connection Not Present";


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
        private CardView cardViewVideos;;

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

                moviePoster = intent.getParcelableExtra(getString(R.string.movie_poster));

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

                linListViewVideos = (LinearListView) rootView.findViewById(R.id.detail_videos);
                cardViewVideos = (CardView) rootView.findViewById(R.id.detail_videos_youtube);
                videoAdapter = new VideoAdapter(getActivity(), new ArrayList<Video>());
                linListViewVideos.setAdapter(videoAdapter);
            }

            return rootView;
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
                FetchVideosTask fetchVideosTask = new FetchVideosTask(getActivity(),videoAdapter,cardViewVideos);
                fetchVideosTask.execute(moviePoster.getMoviePosterId());
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
