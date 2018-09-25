package com.example.ssquare.online_food_order.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Helper.NotificationHelper;
import com.example.ssquare.online_food_order.MainActivity;
import com.example.ssquare.online_food_order.OrderStatus;
import com.example.ssquare.online_food_order.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by S square on 12-06-2018.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            sendNotificationAPI26(remoteMessage);
        else
        sendNotification(remoteMessage);
    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage)
    {
        NotificationHelper helper = null;
        Notification.Builder builder = null;
        PendingIntent pendingIntent;
        //RemoteMessage.Notification notification=remoteMessage.getNotification();
        Map<String,String> notification=remoteMessage.getData();
        assert notification != null;
        String title=notification.get("title");
        String content=notification.get("Message");
        if (Common.currentUser!=null) {
            Intent intent = new Intent(MyFirebaseMessaging.this, OrderStatus.class);
            intent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);

        }
        else
        {
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            helper=new NotificationHelper(this);
            builder=helper.getFoodOrderChannelNotification(title,content,defaultSoundUri);

        }

        helper.getManager().notify(new Random().nextInt(),builder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage)
    {
        Map<String,String> notification=remoteMessage.getData();
        assert notification != null;
        String title=notification.get("title");
        String content=notification.get("Message");
        NotificationHelper helper;
        NotificationCompat.Builder builder;
        PendingIntent pendingIntent;
       // RemoteMessage.Notification notification=remoteMessage.getNotification();
       
        if (Common.currentUser!=null) {
            Intent intent = new Intent(MyFirebaseMessaging.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            assert notification != null;
            builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert noti != null;
            noti.notify(0, builder.build());
        }
        else
        {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            assert notification != null;
            builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert noti != null;
            noti.notify(0, builder.build());
        }
    }
}
