package com.pengsheng.flutterad.liveoauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import   com.pengsheng.flutterad.liveoauth.TTLiveAccountManager;


/**
 * 主要功能：接受授权返回结果的activity（模拟宿主接入抖音授权SDK）
 * <p>
 * <p>
 * 也可通过request.callerLocalEntry = "com.xxx.xxx...activity"; 定义自己的回调类
 */
public class TTDouYinEntryActivity extends Activity implements IApiEventHandler {

    DouYinOpenApi douYinOpenApi;
    public static final String TAG = "DouYinEntryActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        douYinOpenApi = DouYinOpenApiFactory.create(this);
        douYinOpenApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.d(TAG, "授权回调函数：onReq======");
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "Demo-----授权回调函数：onResp---resp.getType()=" + resp.getType());
        if (resp.getType() == CommonConstants.ModeType.SEND_AUTH_RESPONSE) {
            Authorization.Response response = (Authorization.Response) resp;
            if (resp.isSuccess()) {
                Toast.makeText(this, "Demo-----授权成功，获得权限：" + response.grantedPermissions + ",response.authCode:" + response.authCode,
                        Toast.LENGTH_LONG).show();
                TTLiveAccountManager.getInstance().onAuthCodeUpdate(response.authCode, null);
                Log.d("AccountManager", "Demo-----授权成功，获得权限：" + response.grantedPermissions);
            } else if (resp.isCancel()) {
                TTLiveAccountManager.getInstance().onAuthCodeUpdate(null, new IllegalStateException("user cancel"));
                Log.d("AccountManager", "Demo-----取消授权" + response.grantedPermissions);
            } else {
                TTLiveAccountManager.getInstance().onAuthCodeUpdate(null, new IllegalStateException("unknown error"));
                Log.e("AccountManager", "Demo-----授权失败" + response.errorMsg);
            }
        }
        finish();
    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        Log.d(TAG, "Demo-----onErrorIntent：Intent出错=");
        TTLiveAccountManager.getInstance().onAuthCodeUpdate(null, new IllegalStateException("error intent"));
    }
}
