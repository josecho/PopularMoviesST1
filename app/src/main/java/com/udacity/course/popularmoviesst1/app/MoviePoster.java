package com.udacity.course.popularmoviesst1.app;

import android.os.Parcel;
import android.os.Parcelable;

public class MoviePoster implements Parcelable {

    private static final String IMAGE_SIZE_W185 = "http://image.tmdb.org/t/p/w185/";
    private static final String OVER_TEN = "/10";
    private static final String MOVIE_POSTER = "MoviePoster{";
    private static final String ORIGINAL_TITLE = "originalTitle=";
    private static final String POSTER_PATH = ", posterPath=";
    private static final String OVERVIEW = ", overview='";
    private static final String VOTE_AVERAGE = ", voteAverage=";
    private static final String RELEASE_DATE = ", releaseDate='";
    private static final String STRING = "}";
    private String originalTitle;
    private String posterPath;
    private String overview;
    private Double voteAverage;
    private String releaseDate;

    public MoviePoster() {

    }

    @Override
    public String toString() {
        return MOVIE_POSTER +
                ORIGINAL_TITLE + "'" + originalTitle + '\'' +
                POSTER_PATH + "'" + posterPath + '\'' +
                OVERVIEW + overview + '\'' +
                VOTE_AVERAGE + voteAverage +
                RELEASE_DATE + releaseDate + '\'' +
                STRING;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = IMAGE_SIZE_W185 + posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return String.valueOf(voteAverage) + OVER_TEN;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

   /* public Creator<MoviePoster> getCREATOR() {
        return CREATOR;
    }*/

    private MoviePoster(Parcel in){
        originalTitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = (Double) in.readValue(Double.class.getClassLoader());
        releaseDate = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeValue(voteAverage);
        parcel.writeString(releaseDate);
    }

    public static final Parcelable.Creator<MoviePoster> CREATOR = new Parcelable.Creator<MoviePoster>() {
        @Override
        public MoviePoster createFromParcel(Parcel parcel) {
            return new MoviePoster(parcel);
        }

        @Override
        public MoviePoster[] newArray(int i) {
            return new MoviePoster[i];
        }

    };

}
