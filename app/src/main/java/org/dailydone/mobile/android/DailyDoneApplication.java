package org.dailydone.mobile.android;

import android.app.Application;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import org.dailydone.mobile.android.databases.TodoDatabase;
import org.dailydone.mobile.android.factories.DataServiceFactory;
import org.dailydone.mobile.android.rest.IAuthenticationRestOperations;
import org.dailydone.mobile.android.rest.ITodoRestOperations;
import org.dailydone.mobile.android.services.ITodoDataService;
import org.dailydone.mobile.android.util.WebAppHealthUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DailyDoneApplication extends Application {
    public static final String WEB_APP_BACKEND_URL = "http://192.168.178.30:8080/";
    public static final String WEB_APP_BACKEND_API_URL = WEB_APP_BACKEND_URL + "api/";
    public static final String WEB_APP_BACKEND_TODO_URL = WEB_APP_BACKEND_API_URL + "todos/";
    public static final String WEB_APP_AUTH_URL = WEB_APP_BACKEND_API_URL + "users/auth/";

    // To initially determine whether the Web Backend is available the Application itself
    // triggers an initial health check and sets the Live Data in order to allow Observers
    // to react to a connection status change. However, currently this is only used inside
    // the InitialHealthCheckActivity.
    private MutableLiveData<Boolean> isWebBackendAvailable = new MutableLiveData<>();

    private Retrofit retrofit;
    private TodoDatabase todoDatabase;
    private IAuthenticationRestOperations authRestOperations;
    private ITodoRestOperations todoRestOperations;

    private ITodoDataService todoDataService;

    @Override
    public void onCreate() {
        super.onCreate();

        // Using Resteasy I encountered the problem that several classes were missing.
        // During research I found that Resteasy is not optimized for Android.
        // That's why I use the alternative Retrofit.
        retrofit = new Retrofit.Builder()
                .baseUrl(WEB_APP_BACKEND_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();

        todoDatabase = Room.databaseBuilder(this.getApplicationContext(),
                TodoDatabase.class, "todo-db").build();

        authRestOperations = retrofit.create(IAuthenticationRestOperations.class);
        todoRestOperations = retrofit.create(ITodoRestOperations.class);

        executeInitialHealthCheck();
    }

    public TodoDatabase getTodoDatabase() {
        return todoDatabase;
    }

    public IAuthenticationRestOperations getAuthRestOperations() {
        return authRestOperations;
    }

    public ITodoRestOperations getTodoRestOperations() {
        return todoRestOperations;
    }

    public ITodoDataService getTodoDataService() {
        return todoDataService;
    }

    public MutableLiveData<Boolean> getIsWebBackendAvailable() {
        return isWebBackendAvailable;
    }

    private void executeInitialHealthCheck() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        CompletableFuture.supplyAsync(() ->
                                (WebAppHealthUtil.isWebAppAvailable(DailyDoneApplication.WEB_APP_BACKEND_URL)),
                        executor)
                .thenAccept((isWebBackendAvailable) -> {
                    this.initializeDataService(isWebBackendAvailable);
                    this.isWebBackendAvailable.postValue(isWebBackendAvailable);
                });
    }

    // This function uses a Factory to determine the Data Service to be used based on the
    // Availability of the Web Backend. (Just local storage vs. local and remote storage.)
    private void initializeDataService(boolean isWebBackendAvailable) {
        // Do not create the data service multiple times
        if (todoDataService != null) {
            this.todoDataService = new DataServiceFactory(todoDatabase, todoRestOperations)
                    .createDataService(isWebBackendAvailable);
        }
    }
}