package com.example.ssquare.online_food_order.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.ssquare.online_food_order.R;

/**
 * Created by S square on 19-06-2018.
 */

public class NotificationHelper extends ContextWrapper
{

    private static final String S_SQUARE_NAME="FOOD ORDER";
    private static final String S_SQUARE_ID="com.example.ssquare.online_food_order.SSquare";

    private NotificationManager notificationManager;


    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel()
    {
        NotificationChannel squareChannel=new NotificationChannel(S_SQUARE_ID,S_SQUARE_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

        squareChannel.enableLights(false);
        squareChannel.enableVibration(true);
        squareChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(squareChannel);

    }

    public NotificationManager getManager() {
        if (notificationManager==null)
        {
            notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getFoodOrderChannelNotification(String s, String title, PendingIntent contentIntent,
                                                                Uri SoundUri)
    {
        return new Notification.Builder(getApplicationContext(),S_SQUARE_ID)
                .setContentIntent(contentIntent)
                .setContentText(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(SoundUri)
                .setAutoCancel(false);
    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getFoodOrderChannelNotification(String s, String title,
                                                                Uri SoundUri)
    {
        return new Notification.Builder(getApplicationContext(),S_SQUARE_ID)

                .setContentText(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(SoundUri)
                .setAutoCancel(false);
    }
}
