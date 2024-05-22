package com.ndm.da_test.Service;


import android.app.ActivityManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.ndm.da_test.Activity.NotificationActivity;
import com.ndm.da_test.Activity.ThongBao_Activity;
import com.ndm.da_test.Entities.Data;
import com.ndm.da_test.FB.FirebaseHelper;
import com.ndm.da_test.R;
import com.ndm.da_test.Receiver.NotificationDismissedReceiver;
import com.ndm.da_test.Utils.Utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AppFirebaseMessagingService extends FirebaseMessagingService {

    public static final String CHANNEL_ID = "Notification";
    private FirebaseHelper firebaseHelper;

    private Data data;


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.i("xxxxx", "Fcm token is: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        String userId = Utils.getUserId();


        data = new Data();
        data.setType(message.getData().get("type"));
        data.setTitle(message.getData().get("title"));
        data.setBody(message.getData().get("body"));
        if (message.getData().get("latitude") != null & message.getData().get("longitude") != null) {
            data.setLatitude(Double.parseDouble(message.getData().get("latitude")));
            data.setLongitude(Double.parseDouble(message.getData().get("longitude")));
        }
        if (message.getData().get("source") != null) {
            data.setSource(message.getData().get("source"));
        }

        if (message.getData().get("list_question") != null) {
            data.setList_question(message.getData().get("list_question"));
        }


        // In thông tin vào log
        Log.d("Notification", "Type: " + data.getType());
        Log.d("Notification", "Title: " + data.getTitle());
        Log.d("Notification", "Body: " + data.getBody());
        Log.d("Notification", "Latitude: " + data.getLatitude());
        Log.d("Notification", "Longitude: " + data.getLongitude());

        String timestamp = getFormattedTimestamp();
        String type = data.getType();


        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);

        if (type.equals("type_incoming_call")) {
            if (isAppInForeground(getApplicationContext())) {
                showIncomingCallNotification(getApplicationContext(), data.getTitle(), data.getBody());
                Intent intent = new Intent(getApplicationContext(), ThongBao_Activity.class);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, CallingService.class);
                intent.putExtras(bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
        } else {
            showNotification(getApplicationContext(), data.getTitle(), data.getBody());
        }

        firebaseHelper = new FirebaseHelper();
        if (type.equals("noti")) {
            firebaseHelper.saveNotificationData1(userId, data.getType(), data.getTitle(), data.getBody(), data.getSource(), timestamp);
        } else if (type.equals("question")) {
            firebaseHelper.saveNotificationData2(userId, data.getType(), data.getTitle(), data.getBody(), data.getList_question(), timestamp);
        } else {
            firebaseHelper.saveNotificationData(userId, data.getType(), data.getTitle(), data.getBody(), data.getLongitude(), data.getLatitude(), timestamp);
        }
    }


    private String getFormattedTimestamp() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            // Lấy danh sách các process đang chạy
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
            if (runningProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    // Kiểm tra xem process của ứng dụng có trong foreground không
                    if (processInfo.processName.equals(context.getPackageName()) && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        Log.d("AppUtils", "Ứng dụng đang chạy ở background");
                        return true;
                    }
                }
            }
        }
        Log.d("AppUtils", "Ứng dụng đang chạy ở foreground");
        return false;
    }

    public static void showIncomingCallNotification(Context context, String title, String messageBody) {

        // Tạo Intent để gửi broadcast khi người dùng gạt thông báo
        Intent dismissIntent = new Intent(context.getApplicationContext(), NotificationDismissedReceiver.class);
        dismissIntent.setAction("notification_dismissed");
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Tạo NotificationCompat.Builder và thiết lập các thuộc tính
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDeleteIntent(dismissPendingIntent)
                .setAutoCancel(true);

        // Thiết lập âm báo như tiếng chuông điện thoại
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        notificationBuilder.setSound(ringtoneUri);

        // Hiển thị thông báo trên màn hình khóa và thiết lập loại thông báo là cuộc gọi đến
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notificationBuilder.setCategory(NotificationCompat.CATEGORY_CALL);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        // Tạo NotificationManager và gửi thông báo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Call Notifications", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Incoming call notifications");
                channel.setSound(ringtoneUri, null);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    public void showNotification(Context context, String title, String messageBody) {
        // Tạo Intent để khi người dùng nhấn vào thông báo sẽ mở ứng dụng
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(null) // Tắt âm thanh
                .setContentIntent(pendingIntent);

        // Hiển thị thông báo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notifications", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Incoming call notifications");
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0, notificationBuilder.build());
        }
    }


}
