package com.codepath.nytreader.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author yvastavaus.
 */
public class Meta implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public Meta() {
    }

    protected Meta(Parcel in) {
    }

    public static final Parcelable.Creator<Meta> CREATOR = new Parcelable.Creator<Meta>() {
        @Override
        public Meta createFromParcel(Parcel source) {
            return new Meta(source);
        }

        @Override
        public Meta[] newArray(int size) {
            return new Meta[size];
        }
    };
}
