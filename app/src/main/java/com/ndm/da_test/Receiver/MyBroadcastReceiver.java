package com.ndm.da_test.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ndm.da_test.Service.MyFirebaseMessagingService;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Intent serviceIntent = new Intent(context, MyFirebaseMessagingService.class);
            context.startService(serviceIntent);
        }
    }
}