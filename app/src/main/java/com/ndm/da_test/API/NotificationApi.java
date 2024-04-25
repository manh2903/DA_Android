package com.ndm.da_test.API;

import com.ndm.da_test.Entities.Noti;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NotificationApi {
    @POST("firebase/send-notification")
    Call<Noti> sendNotification(@Body Noti notice);
}
