package com.codepath.nytreader.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * author yvastavaus.
 */
public class HeadLine implements Parcelable {

    @SerializedName("main")
    @Expose
    String main;


    public String getMain() {
        return main;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.main);
    }

    public HeadLine() {
    }

    protected HeadLine(Parcel in) {
        this.main = in.readString();
    }

    public static final Parcelable.Creator<HeadLine> CREATOR = new Parcelable.Creator<HeadLine>() {
        @Override
        public HeadLine createFromParcel(Parcel source) {
            return new HeadLine(source);
        }

        @Override
        public HeadLine[] newArray(int size) {
            return new HeadLine[size];
        }
    };
}
