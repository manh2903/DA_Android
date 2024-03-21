package com.ndm.da_test.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ndm.da_test.Activity.Test1_Activity;
import com.ndm.da_test.R;

public class NotificationHelper {
    private static final String CHANNEL_ID = "CallChannel";
    private static final int NOTIFICATION_ID = 123;

    public static void showCallNotification(Context context) {
        // Tạo một Intent để mở ứng dụng khi thông báo được nhấp
        Intent intent = new Intent(context, Test1_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); // Thêm cờ mutability FLAG_UPDATE_CURRENT

        // Tạo một thông báo
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.sharingan)
                .setContentTitle("Incoming Call")
                .setContentText("You have a new call")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Hiển thị thông báo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Call Notifications", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}
