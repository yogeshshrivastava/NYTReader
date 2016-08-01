package com.codepath.nytreader.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * author yvastavaus.
 */
public class ArticleResponse implements Parcelable {
    @SerializedName("meta")
    @Expose
    private Meta type;

    @SerializedName("docs")
    @Expose
    private ArrayList<Docs> docs;

    public ArrayList<Docs> getDocs() {
        return docs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.type, flags);
        dest.writeTypedList(this.docs);
    }

    public ArticleResponse() {
    }

    protected ArticleResponse(Parcel in) {
        this.type = in.readParcelable(Meta.class.getClassLoader());
        this.docs = in.createTypedArrayList(Docs.CREATOR);
    }

    public static final Parcelable.Creator<ArticleResponse> CREATOR = new Parcelable.Creator<ArticleResponse>() {
        @Override
        public ArticleResponse createFromParcel(Parcel source) {
            return new ArticleResponse(source);
        }

        @Override
        public ArticleResponse[] newArray(int size) {
            return new ArticleResponse[size];
        }
    };
}
