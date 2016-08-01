package com.codepath.nytreader.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Contains all the filters for the query search.
 *
 * @author yvastavaus.
 */
public class ArticleFilter implements Parcelable {

    private static final String PREF_KEY = "ArticleFilter.PREF_KEY";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ART, FASHION, SPORTS})
    private @interface newsDeskType {}

    private static final String ART = "art";
    private static final String FASHION = "fashion";
    private static final String SPORTS = "sports";


    private String beginDate;
    private String endDate;
    private String sortOrder;
    private ArrayList<String> newsDesk;

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndDateApiString() {
        if(!TextUtils.isEmpty(endDate)) {
            String[] date = endDate.split("/");
            DecimalFormat formatter = new DecimalFormat("00");
            return date[2] + formatter.format(Integer.parseInt(date[0])) + formatter.format(Integer.parseInt(date[1]));
        }
        return endDate;
    }

    public String getBeginDateApiString() {
        if(!TextUtils.isEmpty(beginDate)) {
            String[] date = beginDate.split("/");
            DecimalFormat formatter = new DecimalFormat("00");
            return date[2] + formatter.format(Integer.parseInt(date[0])) + formatter.format(Integer.parseInt(date[1]));
        }
        return beginDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public ArrayList<String> getNewsDesk() {
        return newsDesk;
    }

    public String getNewsDeskApiString() {
        String newsDeskString = "news_desk:(";
        String query = "";
        for(String item: newsDesk) {
            if(!TextUtils.isEmpty(query)) {
                query += " ";
            }

            if(ART.equalsIgnoreCase(item)) {
                query += "\"Arts\"";
            } else if(FASHION.equalsIgnoreCase(item)) {
                query += "\"Fashion & Style\"";
            } else if(SPORTS.equalsIgnoreCase(item)) {
                query += "\"Sports\"";
            }
        }

        if(!TextUtils.isEmpty(query)) {
            return newsDeskString + query + ")";
        } else {
            return null;
        }
    }

    public void setNewsDesk(ArrayList<String> newsDesk) {
        this.newsDesk = newsDesk;
    }

    public static void writeToPref(ArticleFilter filter) {
        Gson gson = new Gson();
        String filterString = gson.toJson(filter);
        Prefs.putString(PREF_KEY, filterString);
    }

    public static ArticleFilter readPrefs() {
        String articleString = Prefs.getString(PREF_KEY, "");
        if(TextUtils.isEmpty(articleString)) {
            return null;
        } else {
            return new Gson().fromJson(articleString, ArticleFilter.class);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.beginDate);
        dest.writeString(this.endDate);
        dest.writeString(this.sortOrder);
        dest.writeStringList(this.newsDesk);
    }

    public ArticleFilter() {
    }

    protected ArticleFilter(Parcel in) {
        this.beginDate = in.readString();
        this.endDate = in.readString();
        this.sortOrder = in.readString();
        this.newsDesk = in.createStringArrayList();
    }

    public static final Creator<ArticleFilter> CREATOR = new Creator<ArticleFilter>() {
        @Override
        public ArticleFilter createFromParcel(Parcel source) {
            return new ArticleFilter(source);
        }

        @Override
        public ArticleFilter[] newArray(int size) {
            return new ArticleFilter[size];
        }
    };
}
