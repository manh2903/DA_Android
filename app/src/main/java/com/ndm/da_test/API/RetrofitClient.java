package com.ndm.da_test.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 192.168.0.102


// https://assured-definitely-tortoise.ngrok-free.app
public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.0.106:8080/"; // Địa chỉ base URL của API
    private static Retrofit retrofit = null;

    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
