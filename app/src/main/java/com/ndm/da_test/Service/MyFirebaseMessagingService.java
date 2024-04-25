package com.ndm.da_test.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.ndm.da_test.Activity.MapActivity;
import com.ndm.da_test.Activity.ThongBao_Activity;
import com.ndm.da_test.FB.FirebaseHelper;
import com.ndm.da_test.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String CHANNEL_ID = "CallChannel";
    private static final String TAG = "MyFirebaseMsgService";
    private Map<String, String> data;
    private String longitude, latitude;

    private FirebaseHelper firebaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseHelper = new FirebaseHelper();
        Log.d(TAG, "MyFirebaseMessagingService onCreate");
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        if (remoteMessage.getData().size() > 0) {

            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            data = remoteMessage.getData();
            longitude = data.get("longitude");
            latitude = data.get("latitude");
            Log.d(TAG, "Longitude: " + longitude);
            Log.d(TAG, "Latitude: " + latitude);

        }

        if (remoteMessage.getNotification() != null) {


            String notificationTitle = remoteMessage.getNotification().getTitle();
            String notificationBody = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // showCallNotification(getApplicationContext(), notificationTitle, notificationBody);
            showIncomingCallNotification(getApplicationContext(), notificationTitle, notificationBody);


            String timestamp = getFormattedTimestamp();

            firebaseHelper.saveNotificationData(userId, notificationTitle, notificationBody, longitude, latitude, timestamp);


            if (isAppInForeground(getApplicationContext())) {
                // Nếu đang chạy, tự động chuyển sang ThongBao_Activity và gửi dữ liệu title và body
                Intent intent = new Intent(getApplicationContext(), ThongBao_Activity.class);
                intent.putExtra("notificationTitle", remoteMessage.getNotification().getTitle());
                intent.putExtra("notificationBody", remoteMessage.getNotification().getBody());
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

        }
    }

    private boolean isAppInForeground(Context context) {
        // Kiểm tra xem ứng dụng có đang chạy trong foreground không
        // Bạn có thể thực hiện kiểm tra này bằng cách sử dụng một số phương thức như kiểm tra trạng thái của top activity, vv.
        // Ở đây, tôi giả định rằng ứng dụng đang chạy trong foreground và luôn trả về true.
        return true;
    }

 /*   public static void showCallNotification(Context context, String title, String messageBody) {
        Intent intent = new Intent(context.getApplicationContext(), MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        notificationBuilder.setSound(ringtoneUri);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Call Notifications", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Incoming call notifications");
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.setSound(ringtoneUri, null);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0, notificationBuilder.build());
        }

    } */

    private String getFormattedTimestamp() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static void showIncomingCallNotification(Context context, String title, String messageBody) {
        Intent intent = new Intent(context.getApplicationContext(), MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_phone)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(true);



        // Thiết lập âm báo như tiếng chuông điện thoại
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        notificationBuilder.setSound(ringtoneUri);

        // Hiển thị thông báo trên màn hình khóa và thiết lập loại thông báo là cuộc gọi đến
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notificationBuilder.setCategory(NotificationCompat.CATEGORY_CALL);

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


}