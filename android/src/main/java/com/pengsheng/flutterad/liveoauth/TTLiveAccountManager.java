package com.pengsheng.flutterad.liveoauth;

import android.app.Activity;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.DouYinOpenConfig;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import com.bytedance.sdk.openadsdk.live.TTLiveAuthCallback;
import com.bytedance.sdk.openadsdk.live.TTLiveToken;
import com.google.gson.JsonObject;
import com.pengsheng.flutterad.utils.UrlBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Create by WUzejian on 2022/1/19.
 * 抖音账号授权登录管理
 */
public class TTLiveAccountManager {

    private static final String GET_ACCESS_TOKEN_PATH = "https://open.douyin.com/oauth/access_token/";
    private static final String REFRESH_TOKEN_PATH = "https://open.douyin.com/oauth/refresh_token/";
    private String mClientKey = null;
    private String mClientSecret = null;
    private TTLiveAuthCallback currentAuthCallback = null;
    private static TTLiveAccountManager sInstance;
    private OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    String DOUYIN_AUTH_NAME = "Venv Test";

    private TTLiveAccountManager() {
        syncClientKeyAndSecret();
    }

    public static TTLiveAccountManager getInstance() {
        if (sInstance == null) {
            synchronized (TTLiveAccountManager.class) {
                if (sInstance == null) {
                    sInstance = new TTLiveAccountManager();
                }
            }
        }
        return sInstance;
    }

    public void syncClientKeyAndSecret() {
        mClientKey = "awwa5ejfr1lnqdr5";
        mClientSecret = "f58673c062dc19dc1a181908f91db24f";
    }

    public void requestAuth(Activity activity, TTLiveAuthCallback authCallback) {
        syncClientKeyAndSecret();
        currentAuthCallback = authCallback;
        // step1. 通过账号sdk获取auth_code
        DouYinOpenApiFactory.init(new DouYinOpenConfig(mClientKey));
        DouYinOpenApi douyinOpenApi = DouYinOpenApiFactory.create(activity);
        Authorization.Request request = new Authorization.Request();
        request.scope = "user_info,trial.whitelist";
        request.state = "csj_live_sdk_auth"; //用于保持请求和回调的状态，授权请求后原样带回给第三方。
        request.callerLocalEntry = "com.pengsheng.game.liveoauth.TTDouYinEntryActivity";// 设置授权的activity，可用于接收授权回调
        douyinOpenApi.authorize(request);
    }

    public void refreshToken(String refreshToken, TTLiveAuthCallback authCallback) {
        currentAuthCallback = authCallback;
        syncClientKeyAndSecret();
        refreshAccessToken(refreshToken);
    }

    private void refreshAccessToken(String oldRefreshToken) {
        UrlBuilder urlBuilder = new UrlBuilder(REFRESH_TOKEN_PATH);
        urlBuilder.addParam("client_key", mClientKey);
        urlBuilder.addParam("grant_type", "refresh_token");
        urlBuilder.addParam("refresh_token", oldRefreshToken);

        Request request = new Request.Builder().url(urlBuilder.build()).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (currentAuthCallback != null) {
                    currentAuthCallback.onFailed(e);
                }
                resetCallback();
            }

            @Override
            public void onResponse(Call call, Response netResponse) throws IOException {
                boolean isSuccessCallback = netResponse != null && netResponse.isSuccessful();
                final String[] errorMsg = {"unknown"};
                if (isSuccessCallback) {
                    final String body = netResponse.body() != null ? netResponse.body().string() : null;
                    if (!TextUtils.isEmpty(body)) {
                        TTLiveAuthResponse.RefreshTokenResponse refreshTokenResponse =
                                GsonHelper.get().fromJson(body, TTLiveAuthResponse.RefreshTokenResponse.class);
                        if (refreshTokenResponse != null) {
                            JsonObject data = refreshTokenResponse.data;
                            if (data != null) {
                                TTLiveAuthResponse.RefreshToken refreshToken = GsonHelper.get().fromJson(data, TTLiveAuthResponse.RefreshToken.class);
                                if (refreshToken != null) {
                                    //错误。。
                                    if (refreshToken.errorCode != 0) {
                                        isSuccessCallback = false;
                                        errorMsg[0] = "refreshToken errorCode = 0 " + refreshToken.description;
                                    } else if (!TextUtils.isEmpty(refreshToken.accessToken)) {
                                        // step4:获取到access_token，调用直播接口更新token
                                        TTLiveToken newTTLiveToken = new TTLiveToken(DOUYIN_AUTH_NAME, refreshToken.accessToken,
                                                refreshToken.openId,
                                                convertExpiresAt(refreshToken.expiresIn),
                                                refreshToken.refreshToken);
                                        //更新token
                                        TTLiveTokenHelper.getInstance().saveToken(newTTLiveToken);

                                        //回调给外部
                                        if (currentAuthCallback != null) {
                                            currentAuthCallback.onAuth(newTTLiveToken);
                                        }
                                    } else {
                                        isSuccessCallback = false;
                                        errorMsg[0] = "refreshToken.accessToken is null!";
                                    }
                                }
                            } else {
                                isSuccessCallback = false;
                                errorMsg[0] = "refreshToken is null ";
                            }
                        } else {
                            isSuccessCallback = false;
                            errorMsg[0] = "refreshTokenResponse is null ";
                        }

                    } else {
                        isSuccessCallback = false;
                        errorMsg[0] = "network response body is null !";
                    }
                }

                if (!isSuccessCallback && currentAuthCallback != null) {
                    currentAuthCallback.onFailed(new Exception(errorMsg[0]));
                }
            }
        });
    }

    public void onAuthCodeUpdate(String authCode, Throwable throwable) {
        if (!TextUtils.isEmpty(authCode)) {
            getAccessToken(authCode);
        } else {
            if (currentAuthCallback != null) {
                currentAuthCallback.onFailed(throwable);
            }
        }
    }

    private void getAccessToken(String authCode) {
        syncClientKeyAndSecret();
        // 该接口建议放到服务端请求
        UrlBuilder urlBuilder = new UrlBuilder(GET_ACCESS_TOKEN_PATH);
        urlBuilder.addParam("client_key", mClientKey);
        urlBuilder.addParam("client_secret", mClientSecret);
        urlBuilder.addParam("code", authCode);
        urlBuilder.addParam("grant_type", "authorization_code");
        final String[] errorMsg = {"unknown"};
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (currentAuthCallback != null) {
                    currentAuthCallback.onFailed(e);
                }
                resetCallback();
            }

            @Override
            public void onResponse(Call call, Response netResponse) throws IOException {
                boolean isSuccessCallback = netResponse != null && netResponse.isSuccessful();
                if (isSuccessCallback) {
                    final String body = netResponse.body() != null ? netResponse.body().string() : null;
                    if (!TextUtils.isEmpty(body)) {
                        TTLiveAuthResponse.AuthAccessTokenResponse accessTokenResponse =
                                GsonHelper.get().fromJson(body, TTLiveAuthResponse.AuthAccessTokenResponse.class);
                        if (accessTokenResponse != null) {
                            JsonObject data = accessTokenResponse.data;
                            if (data != null) {
                                TTLiveAuthResponse.AuthAccessToken accessTokenData = GsonHelper.get().fromJson(data, TTLiveAuthResponse.AuthAccessToken.class);
                                if (accessTokenData != null) {
                                    //错误。。
                                    if (accessTokenData.errorCode != 0) {
                                        isSuccessCallback = false;
                                        errorMsg[0] ="accessTokenData errorCode = 0 " + accessTokenData.description;
                                    } else if (!TextUtils.isEmpty(accessTokenData.accessToken)) {
                                        // step4:获取到access_token，调用直播接口更新token
                                        TTLiveToken newTTLiveToken = new TTLiveToken(DOUYIN_AUTH_NAME,accessTokenData.accessToken,
                                                accessTokenData.openId,
                                                convertExpiresAt(accessTokenData.expiresIn),
                                                accessTokenData.refreshToken);
                                        //更新token
                                        TTLiveTokenHelper.getInstance().saveToken(newTTLiveToken);
                                        //回调给外部
                                        if (currentAuthCallback != null) {
                                            currentAuthCallback.onAuth(newTTLiveToken);
                                        }
                                    } else {
                                        isSuccessCallback = false;
                                        errorMsg[0] ="accessTokenData is null ";
                                    }
                                }
                            } else {
                                isSuccessCallback = false;
                                errorMsg[0] ="accessTokenResponse.data is null";
                            }
                        } else {
                            isSuccessCallback = false;
                            errorMsg[0] ="accessTokenResponse is null";
                        }

                    } else {
                        isSuccessCallback = false;
                        errorMsg[0] = "network response body is null !"; }
                }

                if (!isSuccessCallback) {
                    if (currentAuthCallback != null) {
                        currentAuthCallback.onFailed(new Exception(errorMsg[0]));
                    }
                }
            }
        });
    }

    private void resetCallback() {
        if (currentAuthCallback != null) {
            currentAuthCallback = null;
        }
    }

    // s -> ms
    private long convertExpiresAt(long expiresIn) {
        if (expiresIn != 0) {
            return System.currentTimeMillis() + expiresIn * 1000;
        } else {
            return 0L;
        }
    }


}
