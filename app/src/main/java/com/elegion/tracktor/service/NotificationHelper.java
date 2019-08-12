package com.elegion.tracktor.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;
import com.elegion.tracktor.ui.map.MainActivity;
import com.elegion.tracktor.util.StringUtil;

public class NotificationHelper {
    public static final String CHANNEL_ID = "counter_service";
    public static final String CHANNEL_NAME = "Counter Service";
    public static final int NOTIFICATION_ID = 101;
    public static final int REQUEST_CODE_LAUNCH = 0;

    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;

    public void createNotification(Service service){

        mNotificationManager = (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        Notification notification = buildNotification();
        service.startForeground(NOTIFICATION_ID, notification);

    }

    private Notification buildNotification() {
        return buildNotification("", "");
    }

    private Notification buildNotification(String time, String distance) {

        if(mNotificationBuilder == null){
            configureNotificationBuilder();
        }
        Context context = App.getContext();

        String message = context.getString(R.string.notify_info, time, distance);

        return mNotificationBuilder
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();
    }

    private void configureNotificationBuilder() {
        Intent notificationIntent = new Intent(App.getContext(), MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(
                App.getContext(), REQUEST_CODE_LAUNCH, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent shutdownIntent = new Intent(App.getContext(), MainActivity.class);
        shutdownIntent.setAction("stop");
        PendingIntent pendingIntent = PendingIntent.getActivity(App.getContext(), 1, shutdownIntent, PendingIntent.FLAG_ONE_SHOT);

        mNotificationBuilder = new NotificationCompat.Builder(App.getContext(), CHANNEL_ID)
                .setContentIntent(contentIntent)
                .addAction(new NotificationCompat.Action(0, App.getContext().getString(R.string.stop), pendingIntent))
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_my_location_white_24dp)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(App.getContext().getString(R.string.route_active))
                .setVibrate(new long[]{0})
                .setColor(ContextCompat.getColor(App.getContext(), R.color.colorAccent));

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (mNotificationManager != null && mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel chan = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(chan);
        }
    }
    public void notifyNotification(long totalSeconds,  double mDistance){
        Notification notification = buildNotification(StringUtil.getTimeText(totalSeconds), StringUtil.getDistanceText(mDistance));
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }


}
