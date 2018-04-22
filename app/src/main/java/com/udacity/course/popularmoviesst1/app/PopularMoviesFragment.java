package com.udacity.course.popularmoviesst1.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.udacity.course.popularmoviesst1.app.adapter.ImageAdapter;
import com.udacity.course.popularmoviesst1.app.model.MoviePoster;


/**
 * Encapsulates fetching the MoviePoster and displaying it as a {@link GridView} layout.
 */
public class PopularMoviesFragment extends Fragment {


    private static final String popularMoviesKey = "popularMoviesKey";

    private static final String INTERNET_CONNECTION_NOT_PRESENT = "Internet Connection Not Present";
    private final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();
    private ImageAdapter imageAdapter;
    GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // save that list to outState for later
        int index = mGridView.getFirstVisiblePosition();
        savedInstanceState.putInt(popularMoviesKey, index);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null){
            final int index = savedInstanceState.getInt(popularMoviesKey);
            mGridView.smoothScrollToPosition(index);
            //mGridView.setSelection(index);
        }
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

        mGridView = rootView.findViewById(R.id.movies_gridview);
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

    @Override
    public void onStart() {
        super.onStart();
        updatePopularMovies();
    }

    private void updatePopularMovies() {
        if (isNetworkAvailable()) {
            FetchMoviePosterTask fetchMoviePosterTask = new FetchMoviePosterTask(getActivity(),imageAdapter);
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String orderBy = sharedPrefs.getString(
                    getString(R.string.pref_orders_key),
                    getString(R.string.pref_order_default));
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

}
