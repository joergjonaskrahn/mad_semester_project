package org.dailydone.mobile.android.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

// These converters are needed because Room doesn`t know how to save List<String> out of the
// box. With these Converters the Lists are stored as JSON Strings.
public class Converters {
    @TypeConverter
    public static String fromList(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<String> toList(String data) {
        if (data == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();

        return gson.fromJson(data, listType);
    }
}