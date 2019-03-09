package com.example.onedayvoca.Chatting;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.StringDef;

import com.example.onedayvoca.ChatRoomActivity;
import com.example.onedayvoca.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by TedPark on 2018. 2. 3..
 */

public class NotificationManager {

    private static final String GROUP_JUNI_PARK = "juniPark";

    public static void createChannel(Context context) {

        NotificationChannelGroup group1 = new NotificationChannelGroup(GROUP_JUNI_PARK, GROUP_JUNI_PARK);
        getManager(context).createNotificationChannelGroup(group1);

        NotificationChannel channelNotice = new NotificationChannel(Channel.NOTICE,
               "Notice", android.app.NotificationManager.IMPORTANCE_HIGH);
        channelNotice.setDescription("Receive notification about our service's notice");
        channelNotice.setGroup(GROUP_JUNI_PARK);
        channelNotice.setLightColor(Color.RED);
        channelNotice.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager(context).createNotificationChannel(channelNotice);


    }

    private static android.app.NotificationManager getManager(Context context) {
        return (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void sendNotification(Context context, int id, @Channel String channel, String title, String body) {

        Intent intent1 = new Intent(context,ChatRoomActivity.class); //인텐트 생성.
//        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent1.putExtra()
        PendingIntent pendingIntent = PendingIntent.getActivity( context,0, intent1, FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context, channel)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setSmallIcon(getSmallIcon())
                .setAutoCancel(true);

        getManager(context).notify(id, builder.build());
    }

    private static int getSmallIcon() {
        return R.drawable.icon;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            Channel.NOTICE
    })
    public @interface Channel {
        String NOTICE = "notice";
    }

    public static void cancel(Context context){
        getManager(context).cancel(1234);
    }

}
