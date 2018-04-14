package com.udacity.course.popularmoviesst1.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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

    private static final String LOG_TAG = VideoAdapter.class.getSimpleName();

    private static final String URL_BASE_VIDEO_YOUTUBE = "http://img.youtube.com/vi/";
    private static final String FINAL_URL = "/0.jpg";
    private final Context vAContext;
    private final LayoutInflater inflater;
    private final Video video = new Video();

    private final List<Video> videos;

    public VideoAdapter(Context context, List<Video> objects) {
        vAContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        videos = objects;
    }

    private Context getContext() {
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
        final Video video = getItem(position);
        viewHolder = (ViewHolder) view.getTag();
        String youtubeURL = URL_BASE_VIDEO_YOUTUBE + video.getKey() + FINAL_URL;
        Glide.with(getContext()).load(youtubeURL).into(viewHolder.imageView);
        viewHolder.nameView.setText(video.getName());
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_VIEW);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                vAContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.getKey())));
                Log.d(LOG_TAG, "launch video with key: " +  video.getKey());
            }
        });
        return view;
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView nameView;

        public ViewHolder(View view) {
            imageView = view.findViewById(R.id.video_image);
            nameView = view.findViewById(R.id.video_name);
        }
    }


}
