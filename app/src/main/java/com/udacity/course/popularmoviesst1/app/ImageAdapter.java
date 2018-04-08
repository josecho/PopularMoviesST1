package com.udacity.course.popularmoviesst1.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.course.popularmoviesst1.app.model.MoviePoster;


class ImageAdapter extends BaseAdapter {

    private final Context context;
    private MoviePoster[] moviePoster;

    public ImageAdapter(Context c, MoviePoster[] moviePoster) {
        context = c;
        this.moviePoster = moviePoster;
    }

    private Context getContext() {
        return context;
    }

    public void setMoviePoster(MoviePoster[] moviePoster) {
        this.moviePoster = moviePoster;
    }

    @Override
    public int getCount() {
        return moviePoster.length;
    }

    @Override
    public Object getItem(int position) {
        return moviePoster[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    //https://developer.android.com/guide/topics/ui/layout/gridview.html
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(getContext());
            imageView.setLayoutParams(new GridView.LayoutParams(240, 288));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        MoviePoster movieP = (MoviePoster) getItem(position);

        //ICONS: https://material.io/guidelines/resources/sticker-sheets-icons.html#sticker-sheets-icons-system-icons
        Picasso.with(getContext())
                .load(movieP.getPosterPath())
                .error(R.drawable.ic_error_black_24dp)
                .placeholder(R.drawable.ic_search_black_24dp)
                .into(imageView);
        return imageView;
    }
}
