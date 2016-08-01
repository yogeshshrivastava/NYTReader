package com.codepath.nytreader.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * author yvastavaus.
 */
public class Docs implements Parcelable {

    @SerializedName("web_url")
    @Expose
    private String webUrl;

    @SerializedName("headline")
    @Expose
    private HeadLine headLine;

    @SerializedName("multimedia")
    @Expose
    private ArrayList<Multimedia> multimedia;

    public String getWebUrl() {
        return webUrl;
    }

    public HeadLine getHeadLine() {
        return headLine;
    }

    public ArrayList<Multimedia> getMultimedia() {
        return multimedia;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.webUrl);
        dest.writeParcelable(this.headLine, flags);
        dest.writeTypedList(this.multimedia);
    }

    public Docs() {
    }

    protected Docs(Parcel in) {
        this.webUrl = in.readString();
        this.headLine = in.readParcelable(HeadLine.class.getClassLoader());
        this.multimedia = in.createTypedArrayList(Multimedia.CREATOR);
    }

    public static final Parcelable.Creator<Docs> CREATOR = new Parcelable.Creator<Docs>() {
        @Override
        public Docs createFromParcel(Parcel source) {
            return new Docs(source);
        }

        @Override
        public Docs[] newArray(int size) {
            return new Docs[size];
        }
    };
}
