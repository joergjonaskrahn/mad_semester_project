package org.dailydone.mobile.android;

import android.app.Application;

import org.dailydone.mobile.android.rest.IAuthenticationRestOperations;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DailyDoneApplication extends Application {
    public static final String WEB_APP_BACKEND_URL = "http://192.168.178.30:8080/";
    public static final String WEB_APP_BACKEND_API_URL = WEB_APP_BACKEND_URL + "api/";
    public static final String WEB_APP_BACKEND_TODO_URL = WEB_APP_BACKEND_API_URL + "todos/";
    public static final String WEB_APP_AUTH_URL = WEB_APP_BACKEND_API_URL + "users/auth/";

    //private ResteasyClient restClient;

    private Retrofit retrofit;
    private IAuthenticationRestOperations authRestService;

    @Override
    public void onCreate() {
        super.onCreate();

        // Using Resteasy I encountered the problem that several classes were missing.
        // During research I found that Resteasy is not optimized for Android.
        // That's why I use the alternative Retrofit.
        retrofit = new Retrofit.Builder()
                .baseUrl(WEB_APP_BACKEND_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authRestService = retrofit.create(IAuthenticationRestOperations.class);
    }

    public IAuthenticationRestOperations getAuthRestService() {
        return authRestService;
    }
}