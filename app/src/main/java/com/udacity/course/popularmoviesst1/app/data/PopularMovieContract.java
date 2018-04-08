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
package com.udacity.course.popularmoviesst1.app.data;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class PopularMovieContract {

    /* Inner class that defines the table contents of the popular movie table */
    public static final class PopularMovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "popularMovies";
        // "id":19404 Popular Movie id as returned by API,
        // Column with the foreign key into the videos and reviews tables.
        public static final String COLUMN_POPULAR_MOVIE_ID = "movie_id";
        //"title":"The Shawshank Redemption"
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        //"poster_path":"\/9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg" in the api
        public static final String COLUMN_POSTER_MAP = "poster_path";
        //A plot synopsis (called overview in the api)
        public static final String COLUMN_OVERWIEW =  "overview";
        //user rating (called "vote_average":8.5 in the api)
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        //called release_date":"1995-10-20" in the api
        public static final String COLUMN_RELEASE_DATE = "release_date";
    }

    public static final class VideosEntry implements BaseColumns {

        public static final String TABLE_NAME = "videos";
        //  Video id as returned by API,
        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_POPULAR_MOVIE_ID = "popular_movie_id";
        public static final String COLUMN_ISO_639_1 = "iso_639_1";
        public static final String COLUMN_ISO_3166_1 = "iso_3166_1";
        public static final String COLUMN_KEY =  "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";
    }

    public static final class ReviewsEntry implements BaseColumns {

        public static final String TABLE_NAME = "reviews";
        //  Review id as returned by API,
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_POPULAR_MOVIE_ID = "popular_movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
    }

}
