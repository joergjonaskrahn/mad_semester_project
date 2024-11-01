package org.dailydone.mobile.android.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class WebAppHealthUtil {
    public static boolean isWebAppAvailable(String urlString) {
        try {
            URL url = new URL(urlString);
            System.out.println("!!!!!!!!!!!!!!!!! responseCode 1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            System.out.println("!!!!!!!!!!!!!!!!! responseCode 2");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            System.out.println("!!!!!!!!!!!!!!!!! responseCode 3");
            System.out.println("!!!!!!!!!!!!!!!!! responseCode");
            System.out.println(responseCode);
            return (responseCode >= 200 && responseCode < 300); // Check whether Status code
        } catch (Exception e) {
            return false;
        }
    }
}