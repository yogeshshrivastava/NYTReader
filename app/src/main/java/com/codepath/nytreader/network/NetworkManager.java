package com.codepath.nytreader.network;

import com.codepath.nytreader.models.Response;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Client to interact with the NYT articles API.
 *
 * @author yvastavaus.
 */
public class NetworkManager {

    private static NYTArticleInterface nytArticleInterface;

    public static NYTArticleInterface getClient() {
        if(nytArticleInterface == null) {
            Retrofit client = new Retrofit.Builder()
                    .baseUrl("http://developer.nytimes.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            nytArticleInterface = client.create(NYTArticleInterface.class);
        }
        return nytArticleInterface;
    }

    public interface NYTArticleInterface {
        @GET("/proxy/https/api.nytimes.com/svc/search/v2/articlesearch.json")
        Call<Response> getArticles(@Query("api-key") String apiKey, @Query("q") String query,
                                   @Query("fq") String filterQuery, @Query("begin_date") String beginDate, @Query("end_date") String endDate
                                    , @Query("sort") String sortType);

        @GET("/proxy/https/api.nytimes.com/svc/search/v2/articlesearch.json")
        Call<Response> getArticlesPages(@Query("api-key") String apiKey, @Query("q") String query,
                                   @Query("fq") String filterQuery, @Query("begin_date") String beginDate, @Query("end_date") String endDate
                ,@Query("sort") String sortType , @Query("page") int page);
    }
}
