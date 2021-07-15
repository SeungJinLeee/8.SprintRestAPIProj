package com.kosmo.fcmmessagingapp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TokenService {

    @FormUrlEncoded
    @POST("token")
    Call<String> token(@Field("token") String token);
}
