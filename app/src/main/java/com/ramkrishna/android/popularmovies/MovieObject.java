package com.ramkrishna.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ramkr on 10-May-16.
 * <p/>
 * Objects of this class hold movie related data, such as title, poster URL, overview, etc.
 */
public class MovieObject implements Parcelable {
    private int id;
    private String posterPath;
    private boolean isAdult;
    private String overview;
    private String releaseDate;
    private String title;
    private String backdropPath;
    private double popularity;
    private double avgVote;

    public MovieObject() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(posterPath);
        dest.writeByte((byte) (isAdult ? 1 : 0));
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(title);
        dest.writeString(backdropPath);
        dest.writeDouble(popularity);
        dest.writeDouble(avgVote);
    }

    private MovieObject(Parcel in) {
        id = in.readInt();
        posterPath = in.readString();
        isAdult = in.readByte() != 0;
        overview = in.readString();
        releaseDate = in.readString();
        title = in.readString();
        backdropPath = in.readString();
        popularity = in.readDouble();
        avgVote = in.readDouble();
    }

    public static final Creator<MovieObject> CREATOR = new Creator<MovieObject>() {
        @Override
        public MovieObject createFromParcel(Parcel in) {
            return new MovieObject(in);
        }

        @Override
        public MovieObject[] newArray(int size) {
            return new MovieObject[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public void setAdult(boolean adult) {
        isAdult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getAvgVote() {
        return avgVote;
    }

    public void setAvgVote(double avgVote) {
        this.avgVote = avgVote;
    }
}
