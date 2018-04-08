package com.udacity.course.popularmoviesst1.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.course.popularmoviesst1.app.R;
import com.udacity.course.popularmoviesst1.app.model.Video;

import java.util.List;

/**
 * Created by josecho on 4/8/18.
 */

public class VideoAdapter extends BaseAdapter {

    private static final String URL_BASE_VIDEO_YOUTUBE = "http://img.youtube.com/vi/";
    private static final String FINAL_URL = "/0.jpg";
    private final Context vAContext;
    private final LayoutInflater inflater;
    private final Video video = new Video();

    private List<Video> videos;

    public VideoAdapter(Context context, List<Video> objects) {
        vAContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        videos = objects;
    }

    public Context getContext() {
        return vAContext;
    }

    public void add(Video object) {
        synchronized (video) {
            videos.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (video) {
            videos.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Video getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = inflater.inflate(R.layout.item_video, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        final Video Video = getItem(position);
        viewHolder = (ViewHolder) view.getTag();
        String youtubeURL = URL_BASE_VIDEO_YOUTUBE + Video.getKey() + FINAL_URL;
        Glide.with(getContext()).load(youtubeURL).into(viewHolder.imageView);
        viewHolder.nameView.setText(Video.getName());
        return view;
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView nameView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.video_image);
            nameView = (TextView) view.findViewById(R.id.video_name);
        }
    }


}
