package org.dailydone.mobile.android;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.dailydone.mobile.android.util.WebAppHealthUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InitialHealthCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_health_check);

        executeInitialHealthCheck();
    }

    private void executeInitialHealthCheck() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Boolean> healthCheck = () -> (
                WebAppHealthUtil.isWebAppAvailable(DailyDoneApplication.WEB_APP_BACKEND_URL)
        );

        Future<Boolean> future = executor.submit(healthCheck);

        new Thread(() -> {
            try {
                Boolean isWebAppAvailable = future.get();
                Intent intent;
                if (isWebAppAvailable) {
                    intent = new Intent(InitialHealthCheckActivity.this, LoginActivity.class);
                } else {
                    intent = new Intent(InitialHealthCheckActivity.this, TodoOverviewActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        }).start();
    }
}