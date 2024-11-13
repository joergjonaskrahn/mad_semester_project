package org.dailydone.mobile.android.rest;

import org.dailydone.mobile.android.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface IAuthenticationRestOperations {

    // Call represents an asynchronous HTTP call
    @PUT("users/auth")
    Call<Boolean> authenticateUser(@Body User user);

    @PUT("users/prepare")
    Call<Boolean> prepare(@Body User user);
}