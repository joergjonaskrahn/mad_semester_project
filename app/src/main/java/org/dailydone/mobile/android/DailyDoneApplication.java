package org.dailydone.mobile.android;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import org.dailydone.mobile.android.infrastructure.databases.TodoDatabase;
import org.dailydone.mobile.android.infrastructure.factories.DataServiceFactory;
import org.dailydone.mobile.android.infrastructure.rest.IAuthenticationRestOperations;
import org.dailydone.mobile.android.infrastructure.rest.ITodoRestOperations;
import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.util.WebAppHealthUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Getter
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

    private TodoDatabase todoDatabase;
    private Retrofit retrofit;

    private IAuthenticationRestOperations authRestOperations;
    private ITodoRestOperations todoRestOperations;

    private ITodoDataService todoDataService;

    @Override
    public void onCreate() {
        super.onCreate();

        // Using Resteasy I encountered the problem that several classes were missing.
        // During research I found that Resteasy is not optimized for Android.
        // That's why I use the alternative Retrofit.

        todoDatabase = Room.databaseBuilder(this.getApplicationContext(),
                TodoDatabase.class, "todo-db").build();

        retrofit = new Retrofit.Builder()
                .baseUrl(WEB_APP_BACKEND_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                // Executor on which the Callbacks of Calls are executed
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build();

        authRestOperations = retrofit.create(IAuthenticationRestOperations.class);
        todoRestOperations = retrofit.create(ITodoRestOperations.class);

        executeInitialHealthCheck();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        todoDataService.shutdownExecutors();
    }

    private void executeInitialHealthCheck() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        CompletableFuture.supplyAsync(() ->
                                (WebAppHealthUtil.isWebAppAvailable(DailyDoneApplication.WEB_APP_BACKEND_URL)),
                        executor)
                .thenAccept((isWebBackendAvailable) -> {
                    this.initializeDataService(isWebBackendAvailable);
                    this.isWebBackendAvailable.postValue(isWebBackendAvailable);
                }).whenComplete((result, throwable) -> {
                    executor.shutdown();
                });
    }

    // This function uses a Factory to determine the Data Service to be used based on the
    // Availability of the Web Backend. (Just local storage vs. local AND remote storage.)
    private void initializeDataService(boolean isWebBackendAvailable) {
        // Defensive programming: Do not create the data service multiple times
        if (this.todoDataService == null) {
            this.todoDataService = new DataServiceFactory(todoDatabase, todoRestOperations)
                    .createDataService(isWebBackendAvailable);
        }
    }
}