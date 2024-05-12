package com.ndm.da_test.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;

import com.ndm.da_test.Activity.IncomingCallActivity;
import com.ndm.da_test.Activity.MainActivity;
import com.ndm.da_test.Entities.Data;
import com.ndm.da_test.R;
import com.ndm.da_test.Receiver.HangUpBroadcast;

import java.io.Serializable;


public class CallingService extends Service {
    public static final String CHANNEL_ID = "Calling channel id";

    private Data data;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                data = (Data) extras.getSerializable("data");
            }
        }
        if (data != null) {
            Log.d("CallingService", "Data: " + data.toString());
            Log.d("CallingService", "Title: " + data.getTitle());
            Log.d("CallingService", "Body: " + data.getBody());
            // Log thêm các trường dữ liệu khác nếu cần
        } else {
            Log.d("CallingService", "Data is null");
        }
        showIncomingCallPopup();
        return START_NOT_STICKY;
    }

    private void showIncomingCallPopup() {

        Intent hangupIntent = new Intent(getApplicationContext(), HangUpBroadcast.class);
        PendingIntent hangupPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, hangupIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent incomingCallIntent = new Intent(getApplicationContext(), IncomingCallActivity.class);
        incomingCallIntent.putExtra("data", data);
        PendingIntent incomingCallPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, incomingCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent answerIntent = new Intent(getApplicationContext(), MainActivity.class);
        answerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent answerPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews customView = new RemoteViews(getPackageName(), R.layout.custom_call);
        customView.setOnClickPendingIntent(R.id.answer, answerPendingIntent);
        customView.setOnClickPendingIntent(R.id.decline, hangupPendingIntent);
        customView.setTextViewText(R.id.tv_title,data.getTitle());
        customView.setTextViewText(R.id.tv_body,data.getBody());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCustomContentView(customView)
                .setFullScreenIntent(incomingCallPendingIntent, true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[]{0, 500, 1000})
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring_stone))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(builder);
        }

        Notification notification = builder.build();
        startForeground(1024, notification);
    }

    private void createNotificationChannel(NotificationCompat.Builder builder) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        CharSequence name = "Incoming call";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring_stone), audioAttributes);
        }
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        builder.setChannelId(CHANNEL_ID);
    }
}
