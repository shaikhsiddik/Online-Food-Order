package com.example.ssquare.online_food_order.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by S square on 12-06-2018.
 */

public class RetrofitClient
{
    private static Retrofit retrofit=null;

    public static Retrofit getClient(String baseURL)
    {
        if (retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


        }
        return retrofit;
    }


}

