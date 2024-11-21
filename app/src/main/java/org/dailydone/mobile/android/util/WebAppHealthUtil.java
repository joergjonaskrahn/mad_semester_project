package org.dailydone.mobile.android.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class WebAppHealthUtil {
    // This method determines whether the Web Backend is available by sending an HTTP
    // request using a specific timeout.
    public static boolean isWebAppAvailable(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 300);
        } catch (Exception e) {
            return false;
        }
    }
}