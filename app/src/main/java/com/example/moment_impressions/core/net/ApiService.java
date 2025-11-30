package com.example.moment_impressions.core.net;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 简单的网络接口：从 JSONPlaceholder 获取示例图片数据
 */
public interface ApiService {
    @GET("photos")
    Call<List<com.example.moment_impressions.core.net.model.Photo>> listPhotos(@Query("_limit") int limit);
}