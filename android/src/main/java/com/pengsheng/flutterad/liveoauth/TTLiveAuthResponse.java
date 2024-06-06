package com.pengsheng.flutterad.liveoauth;


import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Create by WUzejian on 2022/1/19.
 */
public class TTLiveAuthResponse {
    public static class AuthAccessToken {
        @SerializedName("refresh_token")
        String refreshToken;
        @SerializedName("scope")
        String scope;
        @SerializedName("access_token")
        String accessToken;
        @SerializedName("description")
        String description;
        @SerializedName("error_code")
        long errorCode;
        @SerializedName("expires_in")
        long expiresIn;// 单位s
        @SerializedName("open_id")
        String openId;
        @SerializedName("refresh_expires_in")
        long refreshExpiresIn;
    }

    public static class AuthAccessTokenResponse {
        @SerializedName("data")
        JsonObject data;
        @SerializedName("message")
        String message;
    }

    public static class RefreshToken {
        @SerializedName("description")
        String description;
        @SerializedName("error_code")
        long errorCode;
        @SerializedName("expires_in")
        long expiresIn;
        @SerializedName("refresh_token")
        String refreshToken;
        @SerializedName("open_id")
        String openId;
        @SerializedName("refresh_expires_in")
        long refreshExpiresIn;
        @SerializedName("scope")
        String scope;
        @SerializedName("access_token")
        String accessToken;
    }

    public static class RefreshTokenResponse {
        @SerializedName("data")
        JsonObject data;
        @SerializedName("message")
        String message;
    }
}
