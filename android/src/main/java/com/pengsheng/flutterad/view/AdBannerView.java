package com.pengsheng.flutterad.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.pengsheng.flutterad.PluginDelegate;
import com.pengsheng.flutterad.base.BaseAdPage;
import com.pengsheng.flutterad.config.TTAdManagerHolder;
import com.pengsheng.flutterad.mediation.java.utils.Const;
import com.pengsheng.flutterad.utils.UIUtils;

import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.platform.PlatformView;

public class AdBannerView extends BaseAdPage implements PlatformView {

    private final String TAG = AdBannerView.class.getSimpleName();

    private final FrameLayout frameLayout;
    private final PluginDelegate pluginDelegate;
    private final int id;

    private TTNativeExpressAd mBannerAd; // Banner广告对象

    private TTAdNative.NativeExpressAdListener mBannerListener; // 广告加载监听器

    private TTNativeExpressAd.ExpressAdInteractionListener mBannerInteractionListener; // 广告展示监听器

    private TTAdDislike.DislikeInteractionCallback mDislikeCallback; // 接受广告关闭回调

    public AdBannerView(@NonNull Context context, int id, @Nullable Map<String, Object> creationParams, PluginDelegate pluginDelegate) {
        this.id = id;
        this.pluginDelegate = pluginDelegate;
        frameLayout = new FrameLayout(context);
        MethodCall call = new MethodCall("AdBannerView", creationParams);
        showAd(this.pluginDelegate.activity, call);
    }
    @Override
    public void loadAd(MethodCall call) {
        int expressViewWidth = call.argument("width"); // 320f
        int expressViewHeight = call.argument("height"); // 150f
        /** 1、创建AdSlot对象 */
        this.adslot = new AdSlot.Builder()
                .setCodeId(posId)
                .setImageAcceptedSize(UIUtils.dp2px(activity, expressViewWidth), UIUtils.dp2px(activity, expressViewHeight)) // 单位px
                .build();

        /** 2、创建TTAdNative对象 */
        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(activity);
        /** 3、创建加载、展示监听器 */
        initListener();
        /** 4、加载广告 */
        adNativeLoader.loadBannerExpressAd(this.adslot, mBannerListener);
    }

    /**     5、广告加载成功后，设置监听器，展示广告 */
    private void showBannerAd() {
        if (mBannerAd != null) {
            mBannerAd.setExpressInteractionListener(mBannerInteractionListener);
            mBannerAd.setDislikeCallback(activity, mDislikeCallback);
            mBannerAd.uploadDislikeEvent("mediation_dislike_event");
            /** 注意：使用融合功能时，load成功后可直接调用getExpressAdView获取广告view展示，而无需调用render等onRenderSuccess后 */
            View bannerView = mBannerAd.getExpressAdView();
            if (bannerView != null && frameLayout != null) {
                frameLayout.removeAllViews();
                frameLayout.addView(bannerView);
            }
        } else {
            Log.d(Const.TAG, "请先加载广告或等待广告加载完毕后再展示广告");
        }
    }

    /**
     * 注册监听事件
     */
    private void  initListener() {

        // 广告加载监听器
        this.mBannerListener = new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.d(Const.TAG, "banner load fail: errCode: " + i + ", errMsg: " + s);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                if (list != null && list.size() > 0) {
                    Log.d(Const.TAG, "banner load success");
                    mBannerAd = list.get(0);
                    showBannerAd();
                } else {
                    Log.d(Const.TAG, "banner load success, but list is null");
                }
            }
        };

        // 广告展示监听器
        this.mBannerInteractionListener = new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override

            public void onAdClicked(View view, int i) {
                Log.d(Const.TAG, "banner clicked");
            }

            @Override

            public void onAdShow(View view, int i) {
                Log.d(Const.TAG, "banner showed");
            }

            @Override
            public void onRenderFail(View view, String s, int i) {
                // 注意：使用融合功能时，无需调用render，load成功后可调用mBannerAd.getExpressAdView()进行展示。
            }

            @Override
            public void onRenderSuccess(View view, float v, float v1) {
                // 注意：使用融合功能时，无需调用render，load成功后可调用mBannerAd.getExpressAdView()获取view进行展示。
                // 如果调用了render，则会直接回调onRenderSuccess，***** 参数view为null，请勿使用。*****
            }
        };

        // dislike监听器，广告关闭时会回调onSelected
        this.mDislikeCallback = new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() { }
            @Override
            public void onSelected(int i, String s, boolean b) {
                if (frameLayout != null)
                    frameLayout.removeAllViews();
                Log.d(Const.TAG, "banner closed");
            }
            @Override
            public void onCancel() { }
        };
    }

    @Nullable
    @Override
    public View getView() {
        return frameLayout;
    }

    @Override
    public void dispose() {
        frameLayout.removeAllViews();
        /** 6、在onDestroy中销毁广告 */
        if (mBannerAd != null) {
            mBannerAd.destroy();
        }
    }
}
