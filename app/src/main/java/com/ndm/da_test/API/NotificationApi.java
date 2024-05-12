package com.ndm.da_test.API;

import com.ndm.da_test.Entities.Noti_v1;
import com.ndm.da_test.Entities.Noti_v2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NotificationApi {
    @POST("firebase/send-notification-v1")
    Call<Noti_v1> sendNotification(@Body Noti_v1 notice_v1);

    @POST("firebase/send-notification-v2")
    Call<Noti_v2> sendNotification_v2(@Body Noti_v2 notice_v2);
}
