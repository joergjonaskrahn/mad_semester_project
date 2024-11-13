package org.dailydone.mobile.android;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class InitialHealthCheckActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_health_check);

        ((DailyDoneApplication) this.getApplicationContext()).getIsWebBackendAvailable()
                .observe(this, isWebBackendAvailable -> {
                    Intent intent;
                    if (isWebBackendAvailable != null && isWebBackendAvailable) {
                        intent = new Intent(InitialHealthCheckActivity.this, LoginActivity.class);
                    } else {
                        intent = new Intent(InitialHealthCheckActivity.this, TodoOverviewActivity.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }
}