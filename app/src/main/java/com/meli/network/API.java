package com.meli.network;

import com.meli.entities.Description;
import com.meli.entities.Item;
import com.meli.entities.SearchResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {

    @GET("/sites/MLA/search")
    Call<SearchResponse> search(@Query("q") String query, @Query("limit") int limit, @Query("offset") int offset);

    @GET("/items/{item_id}")
    Call<Item> item(@Path("item_id") String item_id);

    @GET("/items/{item_id}/description")
    Call<Description> description(@Path("item_id") String item_id);
}
