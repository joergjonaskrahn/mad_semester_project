package org.dailydone.mobile.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.dailydone.mobile.android.R;

public class Toasts {
    // Since getView called on a Toast created with makeText returns null a custom
    // Layout for warning toasts is created and inflated.
    public static Toast getWarningToast(LayoutInflater layoutInflater,
                                        String message,
                                        Context applicationContext) {
        // "A custom Toast's view is not immediately attached to any parent ViewGroup.
        // Instead, it is handled by the Toast itself. In such cases, passing null as the root
        // is acceptable."
        @SuppressLint("InflateParams") View layout = layoutInflater.inflate(
                R.layout.toast_warning, null);
        TextView textViewWarning = layout.findViewById(R.id.toast_message);
        textViewWarning.setText(message);

        Toast toast = new Toast(applicationContext);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        return toast;
    }
}
