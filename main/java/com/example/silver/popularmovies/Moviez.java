package com.example.silver.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Silver Hein on 13.09.2016.
 */
public class Moviez implements Parcelable {
    private static final String DATE_FORMAT = "yyyy";
    private String mOriginalTitle;
    private String mPosterPath;
    private String mOverview;
    private Double mVoteAverage;
    private String mReleaseDate;

    //Constructor for a movie object.
    public Moviez() {
    }

    //Sets the original title
    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    //Sets the path to the movie poster
    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    //Sets the movie plot
    public void setOverview(String overview) {
        if (!overview.equals("null")) {
            mOverview = overview;
        }
    }

    //Sets the average vote
    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }

    //Sets the releasedate
    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    //Gets the original movie title
    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    //Gets the movie poster via returning the URL string to where the poster can be loadad
    public String getPosterPath() {
        final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";
        return TMDB_POSTER_BASE_URL + mPosterPath;
    }

    //Gets the movie description
    public String getOverview() {
        return mOverview;
    }

    //Gets the average score of the movie
    public Double getVoteAverage(){
        return mVoteAverage;
    }

    //Gets the release date (only year)
    public String getReleaseDate(){
        return mReleaseDate;
    }

    public String getDetailedVoteAverage() {
        return String.valueOf(getVoteAverage());
    }

    //Gets the format of the date
    public String getDateFormat(){
        return DATE_FORMAT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeValue(mVoteAverage);
        dest.writeString(mReleaseDate);
    }

    private Moviez (Parcel in) {
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = (Double) in.readValue(Double.class.getClassLoader());
        mReleaseDate = in.readString();
    }

    public static final Parcelable.Creator<Moviez> CREATOR = new Parcelable.Creator<Moviez>() {
        public Moviez createFromParcel (Parcel source) {
            return new Moviez (source);
        }

        public Moviez[] newArray(int size) {
            return new Moviez[size];
        }
    };
   }
