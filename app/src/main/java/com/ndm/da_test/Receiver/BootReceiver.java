package com.ndm.da_test.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Xử lý khi máy được bật lên
            // Ở đây bạn có thể thực hiện các hành động cần thiết sau khi máy được bật lên, chẳng hạn như kiểm tra có thông báo nào chưa được xử lý.
        }
    }
}
