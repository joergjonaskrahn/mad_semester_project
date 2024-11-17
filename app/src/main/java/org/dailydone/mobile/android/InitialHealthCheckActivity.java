package org.dailydone.mobile.android;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.dailydone.mobile.android.util.Constants;

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

                        // A custom toast layout is necessary since the background color should
                        // be adapted and getView of a Toast created using makeText returns null.
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_warning, null);
                        TextView textViewWarning = layout.findViewById(R.id.toast_message);
                        textViewWarning.setText(getString(R.string.warning_backend_unavailable));

                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }
}