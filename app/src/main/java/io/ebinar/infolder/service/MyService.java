package io.ebinar.infolder.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.blueprint.blueprint.database.query.BPSqlQuery;
import com.blueprint.blueprint.object.BPCollection;
import com.blueprint.blueprint.object.BPObject;
import com.blueprint.blueprint.util.ImageUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import io.ebinar.infolder.MainActivity;
import io.ebinar.infolder.R;


/**
 * Creado Por jorgeacostaalvarado el 20-05-16.
 */
public class MyService extends FirebaseMessagingService {

    private NotificationManager notificationManager = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> hash =  remoteMessage.getData();

        notificationManager =  (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        launchNotification(hash.get("title"),hash.get("message"),"http://www.google.cl");


    }

    private void launchNotification(String title, String content, String url){

        if(notificationManager==null){
            return;
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setData(Uri.parse(url));

        PendingIntent pendIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);

        long[] vibrate ={100l,300l,200l};

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder builder;




        builder = new android.support.v4.app.NotificationCompat.Builder(getBaseContext())
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentIntent(pendIntent)
                .setSmallIcon(R.mipmap.brandcheck_logo)
                .setColor(getResources().getColor(R.color.orange_dark))
                .setVibrate(vibrate)
                .setSound(alarmSound);
        //.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().setSummaryText(content));

        builder.setAutoCancel(true);
        notificationManager.notify("MyTag", 1, builder.build());
    }

}
