package com.codepath.nytreader.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * author yvastavaus.
 */
public class Multimedia implements Parcelable {

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("width")
    @Expose
    private int width;

    @SerializedName("height")
    @Expose
    private int height;

    public String getUrl() {
        return "http://nytimes.com/" + url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public Multimedia() {
    }

    protected Multimedia(Parcel in) {
        this.url = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<Multimedia> CREATOR = new Parcelable.Creator<Multimedia>() {
        @Override
        public Multimedia createFromParcel(Parcel source) {
            return new Multimedia(source);
        }

        @Override
        public Multimedia[] newArray(int size) {
            return new Multimedia[size];
        }
    };
}
