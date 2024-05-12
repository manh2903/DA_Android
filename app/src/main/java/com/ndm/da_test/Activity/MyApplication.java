package com.ndm.da_test.Activity;

import android.app.Activity;
import android.app.Application;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;




import com.ndm.da_test.Service.LocationTrackingService;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: MyApplication");

        // Khởi động dịch vụ

    }


}

