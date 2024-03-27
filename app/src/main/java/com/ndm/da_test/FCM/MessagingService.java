package com.ndm.da_test.FCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ndm.da_test.Activity.MainActivity;
import com.ndm.da_test.Activity.MyApplication;
import com.ndm.da_test.R;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

    /*    RemoteMessage.Notification notification = message.getNotification();
        if(notification == null)
        {
            return;
        }
        String title = notification.getTitle();
        String body = notification.getBody(); */


        Map<String , String>  stringMap = message.getData();
        if (stringMap == null)
        {
            return;
        }
        String title = stringMap.get("key_1");
        String body = stringMap.get("key_2");
        sendNotification(title,body);

    }

    private void sendNotification(String title, String body){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuidler = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        Notification notification = notificationBuidler.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null)
        {
            notificationManager.notify(1,notification);
        }

    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("abc",token);
        super.onNewToken(token);
    }
}
