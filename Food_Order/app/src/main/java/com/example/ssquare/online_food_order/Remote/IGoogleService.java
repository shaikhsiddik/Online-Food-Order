package com.example.ssquare.online_food_order.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by S square on 29-06-2018.
 */

public interface IGoogleService
{
    @GET
    Call<String> getAddressName(@Url String url);

    @GET
    Call<String> getLocationFromAddress(@Url String url);

    @GET("maps/api/directions/json")
    Call<String> getDirection(@Query("origin") String origin,@Query("destination") String destination);
}
