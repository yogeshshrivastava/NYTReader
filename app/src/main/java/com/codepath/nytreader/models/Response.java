package com.codepath.nytreader.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * author yvastavaus.
 */
public class Response implements Parcelable {

    @SerializedName("response")
    @Expose
    private ArticleResponse response;

    public ArticleResponse getResponse() {
        return response;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.response, flags);
    }

    public Response() {
    }

    protected Response(Parcel in) {
        this.response = in.readParcelable(ArticleResponse.class.getClassLoader());
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel source) {
            return new Response(source);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
}
