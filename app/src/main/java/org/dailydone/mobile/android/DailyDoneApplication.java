package org.dailydone.mobile.android;

import android.app.Application;

public class DailyDoneApplication extends Application {
    public static final String WEB_APP_BACKEND_URL = "http://192.168.178.30:8080";
    public static final String WEB_APP_BACKEND_API_URL = WEB_APP_BACKEND_URL + "/api";
    public static final String WEB_APP_BACKEND_TODO_URL = WEB_APP_BACKEND_API_URL + "/todos";

    @Override
    public void onCreate() {
        super.onCreate();
    }

}