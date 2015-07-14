package com.kuaikan.comic.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by a on 2015/3/26.
 */
public class GsonUtil {

    private static final Gson mGsonInstance = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Object src) {
        return mGsonInstance.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT){
        return mGsonInstance.fromJson(json, classOfT);
    }
}
