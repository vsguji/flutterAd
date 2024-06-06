package com.pengsheng.flutterad.liveoauth;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * Create by WUzejian on 2022/1/19.
 */
public class GsonHelper {

    public static Gson get() {
        return builder().create();
    }

    public static Gson getDefault() {
        return new Gson();
    }

    public static JsonParser parser() {
        return new JsonParser();
    }

    public static GsonBuilder builder() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    }
}
