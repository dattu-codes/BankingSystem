package com.bank.mobile.api;

import com.bank.mobile.api.models.AccountResponse;
import com.bank.mobile.api.models.AuthRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<AccountResponse> login(@Body AuthRequest request);

    @POST("auth/register")
    Call<AccountResponse> register(@Body AuthRequest request);
}
