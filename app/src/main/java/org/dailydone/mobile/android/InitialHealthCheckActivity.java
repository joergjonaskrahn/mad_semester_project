package org.dailydone.mobile.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.dailydone.mobile.android.util.Toasts;

public class InitialHealthCheckActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_health_check);

        DailyDoneApplication dailyDoneApplication = (DailyDoneApplication) getApplicationContext();

        dailyDoneApplication.getIsWebBackendAvailable()
                .observe(this, isWebBackendAvailable -> {
                    Intent intent;
                    if (isWebBackendAvailable != null && isWebBackendAvailable) {
                        intent = new Intent(InitialHealthCheckActivity.this, LoginActivity.class);
                    } else {
                        intent = new Intent(InitialHealthCheckActivity.this, TodoOverviewActivity.class);

                        Toast toast = Toasts.getWarningToast(getLayoutInflater(),
                                getString(R.string.warning_backend_unavailable),
                                getApplicationContext());
                        toast.show();
                    }

                    // Prevent navigation back to this activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    // Destroying this activity to prevent navigation to this.
                    finish();
                });
    }
}