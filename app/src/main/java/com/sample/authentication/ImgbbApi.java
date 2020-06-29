package com.sample.authentication;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ImgbbApi {
    @Multipart
    @Headers("Accept: application/json")
    @POST("/1/upload")
    Call<ImgbbResult> uploadImages(@Query("key") String key, @Part MultipartBody.Part file, @Query("expiration") Integer expiration);
}
