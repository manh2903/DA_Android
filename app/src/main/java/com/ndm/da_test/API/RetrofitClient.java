package com.ndm.da_test.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//http://localhost:8080/firebase/send-notification


public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.1.231:8080/"; // Địa chỉ base URL của API
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
