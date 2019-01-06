package com.meli.network;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestUtil {
    private static RestUtil self;
    private Retrofit retrofit;

    private RestUtil() {
        String API_BASE_URL = "https://api.mercadolibre.com";
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(JacksonConverterFactory.create());
        retrofit = builder.build();
    }

    public static RestUtil getInstance() {
        if (self == null) {
            synchronized (RestUtil.class) {
                if (self == null) {
                    self = new RestUtil();
                }
            }
        }
        return self;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

}