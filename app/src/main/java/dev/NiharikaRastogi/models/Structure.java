package dev.NiharikaRastogi.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Niharika Rastogi on 29-12-2015.
 */
public class Structure implements Parcelable {
    public static final Creator<Structure> CREATOR = new Creator<Structure>() {
        @Override
        public Structure createFromParcel(Parcel in) {
            return new Structure(in);
        }

        @Override
        public Structure[] newArray(int size) {
            return new Structure[size];
        }
    };
    public int id;
    public String title;
    public String posterPath;
    public String popularity;
    public String rating;
    public String overview;
    public String release_date;
    public String backdrop_path;

    public Structure(int id, String title, String posterPath, String popularity, String rating, String overview, String release_date, String backdrop_path) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.rating = rating;
        this.overview = overview;
        this.release_date = release_date;
        this.backdrop_path = backdrop_path;
    }

    protected Structure(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        popularity = in.readString();
        rating = in.readString();
        overview = in.readString();
        release_date = in.readString();
        backdrop_path = in.readString();
    }

    @Override
    public String toString() {
        return id + "*" + title + "*" + posterPath + "*" + popularity + "*" + rating + "*" + overview + "*" + release_date + "*" + backdrop_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(popularity);
        dest.writeString(rating);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(backdrop_path);
    }
}