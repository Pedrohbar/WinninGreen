package com.example.winningreen.api;


import com.example.winningreen.api.service.ArduinoCloudApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    // URL base da API do Arduino Cloud
    private static final String BASE_URL = "https://api2.arduino.cc/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ArduinoCloudApiService getApiService() {
        return getClient().create(ArduinoCloudApiService.class);
    }
}
