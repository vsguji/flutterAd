package com.pengsheng.flutterad.liveoauth;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.bytedance.sdk.openadsdk.live.TTLiveAuthCallback;
import com.bytedance.sdk.openadsdk.live.ITTLiveTokenInjectionAuth;
import com.bytedance.sdk.openadsdk.live.TTLiveToken;

import java.util.Map;



/**
 * Create by WUzejian on 2022/1/19.
 * 实现直播抖音授权登录功能
 */
public class TTInjectionAuthImpl implements ITTLiveTokenInjectionAuth {
    private static TTLiveToken token = null;
    private static boolean userAuth = false;
    private static String DOUYIN_AUTH_NAME = "Venv Test";

    @Override
    public TTLiveToken getTokenInfo() {
        TTLiveToken cacheToken = getCacheToken();
        Log.d("tokenShow", "getTokenInfo() cacheToken: " + (cacheToken == null ? null : cacheToken.toString()));
        //TODO:测试值 --吴泽坚
        return getCacheToken() != null ? new TTLiveToken(DOUYIN_AUTH_NAME, getAccessToken(), getOpenId(), getExpireAt(), null) : null;
    }

    private TTLiveToken getCacheToken() {
        if (!userAuth) {
            return null;
        }

        TTLiveToken ttLiveToken = TTLiveTokenHelper.getInstance().fetchToken();
        if (token == null || (!TextUtils.isEmpty(token.accessToken) && token.accessToken.equals(ttLiveToken.accessToken))) {
            token = ttLiveToken;
        }
        return token;
    }

    @Override
    public boolean isLogin() {
        return true;
    }


    @Override
    public void onTokenInvalid(TTLiveToken tokenInfo, final TTLiveAuthCallback callback, Activity activity, Map<String, String> map) {
        String currentAccessToken = tokenInfo != null ? tokenInfo.accessToken : "";

        TTLiveAuthCallback authCallback = new TTLiveAuthCallback() {
            @Override
            public void onAuth(TTLiveToken accessToken) {
                userAuth = true;
                token = accessToken;

                if (callback != null && accessToken != null) {
                    callback.onAuth(new TTLiveToken(DOUYIN_AUTH_NAME,
                            TextUtils.isEmpty(accessToken.openId) ? "" : accessToken.openId,
                            TextUtils.isEmpty(accessToken.accessToken) ? "" : accessToken.accessToken,
                            accessToken.expireAt, null)
                    );
                }
            }

            @Override
            public void onFailed(Throwable error) {
                if (callback != null) {
                    callback.onFailed(error);
                }
            }
        };


        //看是否需要刷新 token
        if (!TextUtils.isEmpty(currentAccessToken) && shouldRefresh(currentAccessToken)) {
            TTLiveAccountManager.getInstance().refreshToken(token.refreshToken, authCallback);
        } else {
            if (callback != null) {
                TTLiveAccountManager.getInstance().requestAuth(activity, authCallback);
            }
        }
    }

    private String getOpenId() {
        return token != null ? token.openId : "";
    }

    private String getAccessToken() {
        return token != null ? token.accessToken : "";
    }

    private long getExpireAt() {
        if (TTLiveTokenHelper.getInstance().enableToken() && !userAuth) {
            return 0L;
        } else {
            return token != null ? token.expireAt : 0L;
        }
    }

    private boolean shouldRefresh(String lastToken) {
        return token != null && !TextUtils.isEmpty(lastToken)
                && lastToken.equals(token.accessToken)
                && !TextUtils.isEmpty(token.refreshToken)
                && token.expireAt < System.currentTimeMillis();
    }
}
