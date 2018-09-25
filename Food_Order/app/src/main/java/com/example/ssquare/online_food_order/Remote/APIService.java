package com.example.ssquare.online_food_order.Remote;

import com.example.ssquare.online_food_order.Model.DataMessage;
import com.example.ssquare.online_food_order.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by S square on 12-06-2018.
 */

public interface APIService
{
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAD9ZbzEU:APA91bHacVrJZQHmjQrr6_5nzEtK9RJra3Ca-3fcBhh5XoYCOrDfAJmjSl_iJTxKQmKUytvMdETs4bGkJYXDTH1laqE6pu7fXKKsyfc9E3DMgnbkijTM2HpYHAHFR1PYpwGLMLCxIs44"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
