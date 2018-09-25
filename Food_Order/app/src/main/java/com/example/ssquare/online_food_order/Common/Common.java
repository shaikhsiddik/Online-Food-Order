package com.example.ssquare.online_food_order.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;

import com.example.ssquare.online_food_order.Model.User;
import com.example.ssquare.online_food_order.Remote.APIService;
import com.example.ssquare.online_food_order.Remote.GoogleRetrofitClient;
import com.example.ssquare.online_food_order.Remote.IGoogleService;
import com.example.ssquare.online_food_order.Remote.RetrofitClient;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Retrofit;

/**
 * Created by S square on 04-06-2018.
 */

public class Common
{
    public static  User currentUser;
    public static  String DELETE="Delete";
    public static  String USER_KEY="User";
    public static  String PWD_KEY="Password";
    private static final String BASE_URL="https://fcm.googleapis.com/";
    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";
    public static String PHONE_TEXT="userPhone";
    public static String currentKey;

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
    public static IGoogleService getGoogleMapAPI()
    {
        return GoogleRetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static String convertCodeToStatus(String code)
    {
        if (code!=null && code.equals("0"))
            return "Placed";
        else if (code!=null && code.equals("1"))
            return "On My Way";
        else if (code!=null && code.equals("2"))
            return "Shipping";
        else
            return "Shipped";
    }


    public static  boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null)
        {
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();


                if (info != null)
                {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }

                }

        }
        return false;
    }



    public static BigDecimal formatCurrency(String amount, Locale locale) throws java.text.ParseException {
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        if (format instanceof DecimalFormat)
            ((DecimalFormat)format).setParseBigDecimal(true);
        return (BigDecimal)format.parse(amount.replace("[^\\d.,]",""));
    }


}
