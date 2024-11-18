package org.dailydone.mobile.android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.dailydone.mobile.android.R;

public class Toasts {
    // A custom toast layout is necessary since the background color should
    // be adapted and getView of a Toast created using makeText returns null.
    public static Toast getWarningToast(LayoutInflater layoutInflater,
                                        String message,
                                        Context applicationContext) {
        View layout = layoutInflater.inflate(R.layout.toast_warning, null);
        TextView textViewWarning = layout.findViewById(R.id.toast_message);
        textViewWarning.setText(message);

        Toast toast = new Toast(applicationContext);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        return toast;
    }
}
