package com.pengsheng.flutterad.liveoauth;

import android.content.Context;
import android.content.SharedPreferences;

import com.bytedance.sdk.openadsdk.live.TTLiveConstants;
import com.bytedance.sdk.openadsdk.live.TTLiveToken;
import com.pengsheng.flutterad.DemoApplication;
//import com.pengsheng.flutterad.DemoApplication;

/**
 * Create by WUzejian on 2022/1/19.
 */

public class TTLiveTokenHelper {
    public static final String SP_CSJ_TOKEN = "csj_test";
    private final SharedPreferences mSp;

    private static final class Holder {
        private static final TTLiveTokenHelper INSTANCE = new TTLiveTokenHelper();
    }

    public static TTLiveTokenHelper getInstance() {
        return Holder.INSTANCE;
    }

    private TTLiveTokenHelper() {
        final Context appContext = DemoApplication.getAppContext();
        mSp = appContext.getSharedPreferences(SP_CSJ_TOKEN, Context.MODE_PRIVATE);
    }


    public void saveToken(TTLiveToken TTLiveToken) {
        final SharedPreferences.Editor edit = mSp.edit();
        if (edit != null) {
            edit.putString("access_token", TTLiveToken.accessToken);
            edit.putString("open_id", TTLiveToken.openId);
            edit.putLong("expire_at", TTLiveToken.expireAt);
            edit.putString("refresh_token", TTLiveToken.refreshToken);
            edit.commit();
        }
    }

    public TTLiveToken fetchToken() {
        return new TTLiveToken(TTLiveConstants.DOUYIN_AUTH_NAME, mSp.getString("access_token", ""),
                mSp.getString("open_id", ""),
                mSp.getLong("expire_at", 0L),
                mSp.getString("refresh_token", ""));
    }

    public void clearToken(){
        final SharedPreferences.Editor edit = mSp.edit();
        if (edit != null) {
            edit.putString("access_token", null);
            edit.putString("open_id", null);
            edit.putLong("expire_at", 0);
            edit.putString("refresh_token", null);
            edit.commit();
        }
    }


    public void enableToken(boolean enable) {
        final SharedPreferences.Editor edit = mSp.edit();
        if (edit != null) {
            edit.putBoolean("enable_token", enable);
            edit.commit();
        }
    }

    public boolean enableToken() {
        return mSp.getBoolean("enable_token", true);
    }


    public int getAuthType() {
        return mSp.getInt("auth_type", 0);
    }

    public void setUseHostAuth(boolean enable) {
        final SharedPreferences.Editor edit = mSp.edit();
        if (edit != null) {
            edit.putBoolean("use_host_auth", enable);
            edit.commit();
        }
    }

    public boolean useHostAuth() {
        return mSp.getBoolean("use_host_auth", false);
    }
}


