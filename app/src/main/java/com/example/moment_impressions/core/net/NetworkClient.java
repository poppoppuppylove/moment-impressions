package com.example.moment_impressions.core.net;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 客户端，提供 ApiService 单例
 */
public class NetworkClient {
    private static volatile ApiService service;

    public static ApiService getService() {
        if (service == null) {
            synchronized (NetworkClient.class) {
                if (service == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://jsonplaceholder.typicode.com/")
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    service = retrofit.create(ApiService.class);
                }
            }
        }
        return service;
    }
}