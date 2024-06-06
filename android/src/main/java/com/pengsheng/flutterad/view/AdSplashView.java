package com.pengsheng.flutterad.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.pengsheng.flutterad.PluginDelegate;
import com.pengsheng.flutterad.base.BaseAdPage;
import com.pengsheng.flutterad.event.AdEvent;
import com.pengsheng.flutterad.event.AdEventAction;
import com.pengsheng.flutterad.event.AdEventHandler;
import com.pengsheng.flutterad.utils.UIUtils;
import com.qq.e.comm.adevent.ADEvent;

import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.platform.PlatformView;

/**
 * flutter view
 * 开屏视图
 */
public class AdSplashView extends BaseAdPage implements PlatformView, TTAdNative.CSJSplashAdListener, TTAdDislike.DislikeInteractionCallback {

    private final String TAG = AdSplashView.class.getSimpleName();
    private final FrameLayout frameLayout;
    private final PluginDelegate pluginDelegate;
    private final int id;

    private TTNativeExpressAd splash;

    // 单条开屏广告,处理监听事件
    private CSJSplashAd.SplashAdListener mCSJSplashInteractionListener;

    public AdSplashView(@NonNull Context context, int id, @Nullable Map<String, Object> creationParams, PluginDelegate pluginDelegate) {
        this.id = id;
        this.pluginDelegate = pluginDelegate;
        frameLayout = new FrameLayout(context);
        MethodCall call = new MethodCall("AdSplashView", creationParams);
        showAd(this.pluginDelegate.activity, call);
    }

    @Nullable
    @Override
    public View getView() {
        return frameLayout;
    }

    @Override
    public void dispose() {
        disposeAd();
    }

    @Override
    public void loadAd(MethodCall call) {
        // 获取请求模板广告素材的尺寸
        int expressViewWidth = call.argument("width");
        int expressViewHeight = call.argument("height");
        this.adslot = new AdSlot.Builder()
                .setCodeId(posId)
                .setImageAcceptedSize(UIUtils.dp2px(activity,expressViewWidth), UIUtils.dp2px(activity,expressViewHeight))
                .build();
        // 加载广告
        adNativeLoader.loadSplashAd(adslot,this,1000);
        // 注册监听事件
        initListener();
    }

    /**
     * 注册监听事件
     */
    private void  initListener() {
        this.mCSJSplashInteractionListener = new CSJSplashAd.SplashAdListener() {
            @Override
            public void onSplashAdShow(CSJSplashAd csjSplashAd) {
                Log.d(TAG, "splash show");
                pluginDelegate.addEvent(new AdEvent(posId,AdEventAction.onAdLoaded).toMap());
            }

            @Override
            public void onSplashAdClick(CSJSplashAd csjSplashAd) {
                Log.d(TAG, "splash click");
                pluginDelegate.addEvent(new AdEvent(posId,AdEventAction.onAdClicked).toMap());
                disposeAd();
            }

            @Override
            public void onSplashAdClose(CSJSplashAd csjSplashAd, int i) {
                Log.d(TAG, "splash close");
               // pluginDelegate.activity.finish();
                pluginDelegate.addEvent(new AdEvent(posId,AdEventAction.onAdClosed).toMap());
                disposeAd();
            }
        };
    }

    /**
     * 销毁广告
     */
    private void disposeAd() {
        frameLayout.removeAllViews();
        if (splash != null) {
            splash.setExpressInteractionListener(null);
            splash.destroy();
        }
    }

    @Override
    public void onShow() {
        Log.i(TAG, "Dislike onShow");
    }

    @Override
    public void onSelected(int i, String s, boolean b) {
        Log.i(TAG, "Dislike onSelected");
        // 添加广告事件
        disposeAd();
        sendEvent(AdEventAction.onAdClosed);
    }

    @Override
    public void onCancel() {
        Log.i(TAG, "Dislike onCancel");
    }

    @Override
    public void onSplashLoadSuccess(CSJSplashAd csjSplashAd) {
        Log.d(TAG, "splash load success");
        sendEvent(AdEventAction.onAdLoaded);
    }

    @Override
    public void onSplashLoadFail(CSJAdError csjAdError) {
        Log.d(TAG, "splash load fail, errCode: " + csjAdError.getCode() + ", errMsg: " + csjAdError.getMsg());
        sendErrorEvent(csjAdError.getCode(),csjAdError.getMsg());
        disposeAd();
    }

    @Override
    public void onSplashRenderSuccess(CSJSplashAd csjSplashAd) {
        if (this.pluginDelegate.activity.isFinishing()) {
            disposeAd();
            return;
        }
        csjSplashAd.setSplashAdListener(this.mCSJSplashInteractionListener);
        View splashView = csjSplashAd.getSplashView();
        if (splashView == null) {
            disposeAd();
            return;
        }
        UIUtils.removeFromParent(splashView);
        frameLayout.removeAllViews();
        frameLayout.addView(splashView);
        // 添加广告事件
        sendEvent(AdEventAction.onAdPresent);
    }

    @Override
    public void onSplashRenderFail(CSJSplashAd csjSplashAd, CSJAdError csjAdError) {
        Log.d(TAG, "splash render fail, errCode: " + csjAdError.getCode() + ", errMsg: " + csjAdError.getMsg());
        sendErrorEvent(csjAdError.getCode(),csjAdError.getMsg());
    }
}
