package com.ndm.da_test.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationDismissedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (action.equals("notification_dismissed")) {
                // Xử lý khi thông báo bị gạt bỏ
                Log.d("NotificationDismissed", "Notification dismissed");
            }
        }
    }
}
