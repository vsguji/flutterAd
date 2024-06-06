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
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.mediation.ad.MediationExpressRenderListener;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationNativeManager;
import com.pengsheng.flutterad.PluginDelegate;
import com.pengsheng.flutterad.base.BaseAdPage;
import com.pengsheng.flutterad.config.TTAdManagerHolder;
import com.pengsheng.flutterad.mediation.java.utils.Const;
import com.pengsheng.flutterad.mediation.java.utils.FeedAdUtils;
import com.pengsheng.flutterad.utils.UIUtils;

import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.platform.PlatformView;

public class AdFeedView extends BaseAdPage implements PlatformView,TTAdNative.FeedAdListener{

    private final String TAG = AdFeedView.class.getSimpleName();

    private final FrameLayout frameLayout;
    private final PluginDelegate pluginDelegate;
    private final int id;

    private TTFeedAd mTTFeedAd; // Feed广告对象

    private TTAdNative.FeedAdListener mFeedAdListener; // 广告加载监听器
    private MediationExpressRenderListener mExpressAdInteractionListener; // 模板广告展示监听器

    private TTNativeAd.AdInteractionListener mAdInteractionListener; // 自渲染广告展示监听器


    public AdFeedView(@NonNull Context context, int id, @Nullable Map<String, Object> creationParams, PluginDelegate pluginDelegate) {
        this.id = id;
        this.pluginDelegate = pluginDelegate;
        frameLayout = new FrameLayout(context);
        MethodCall call = new MethodCall("AdFeedView", creationParams);
        showAd(this.pluginDelegate.activity, call);
    }

    @Override
    public void loadAd(MethodCall call) {
        int expressViewWidth = call.argument("width"); // 300
        int expressViewHeight = call.argument("height"); // 250
        int screenWitdh = UIUtils.getScreenWidthInPx(activity);
        int feedHeight = screenWitdh / 300 * expressViewHeight;
        /** 1、创建AdSlot对象 */
        this.adslot = new AdSlot.Builder()
                .setCodeId(posId)
                .setImageAcceptedSize(UIUtils.dp2px(activity, expressViewWidth), UIUtils.dp2px(activity, expressViewHeight)) // 单位px
                .setAdCount(1) // 请求广告数量为1到3条 （优先采用平台配置的数量）
                .build();
        /** 2、创建TTAdNative对象 */
        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(activity);
        /** 3、创建加载、展示监听器 */
        initListener();
        /** 4、加载广告 */
        adNativeLoader.loadFeedAd(adslot, this);
    }

    // 广告加载成功后，展示广告
    private void showFeedAd() {
        if (this.mTTFeedAd == null) {
            Log.i(Const.TAG, "请先加载广告或等待广告加载完毕后再调用show方法");
            return;
        }
        mTTFeedAd.uploadDislikeEvent("mediation_dislike_event");
        /** 5、展示广告 */
        MediationNativeManager manager = mTTFeedAd.getMediationManager();
        if (manager != null) {
            if (manager.isExpress()) { // --- 模板feed流广告
                mTTFeedAd.setExpressRenderListener(mExpressAdInteractionListener);
                mTTFeedAd.render(); // 调用render方法进行渲染，在onRenderSuccess中展示广告

            } else {                   // --- 自渲染feed流广告
                // 自渲染广告返回的是广告素材，开发者自己将其渲染成view
                View feedView = FeedAdUtils.getFeedAdFromFeedInfo(mTTFeedAd, activity, null, mAdInteractionListener);
                if (feedView != null) {
                    UIUtils.removeFromParent(feedView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(feedView);
                }
            }
        }
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

    /**
     * 销毁广告
     */
    private void disposeAd() {
        frameLayout.removeAllViews();
        if (mTTFeedAd != null) {
            mTTFeedAd.setExpressRenderListener(null);
            mTTFeedAd.destroy();
        }
    }

    /**
     * 注册监听器
     */
    protected void initListener() {
        // 模板广告展示监听器
        this.mExpressAdInteractionListener = new MediationExpressRenderListener() {
            @Override
            public void onRenderFail(View view, String s, int i) {
                Log.d(Const.TAG, "feed express render fail, errCode: " + i + ", errMsg: " + s);
            }

            @Override
            public void onAdClick() {
                Log.d(Const.TAG, "feed express click");
            }

            @Override
            public void onAdShow() {
                Log.d(Const.TAG, "feed express show");
            }

            @Override
            public void onRenderSuccess(View view, float v, float v1, boolean b) {
                Log.d(Const.TAG, "feed express render success");
                if (mTTFeedAd != null) {
                    View expressFeedView = mTTFeedAd.getAdView(); // *** 注意不要使用onRenderSuccess参数中的view ***
                    UIUtils.removeFromParent(expressFeedView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(expressFeedView);
                }
            }
        };

        // 自渲染广告展示监听器
        this.mAdInteractionListener = new TTNativeAd.AdInteractionListener() {
            @Override

            public void onAdClicked(View view, TTNativeAd ttNativeAd) {
                Log.d(Const.TAG, "feed click");
            }

            @Override

            public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
                Log.d(Const.TAG, "feed creative click");
            }

            @Override

            public void onAdShow(TTNativeAd ttNativeAd) {
                Log.d(Const.TAG, "feed show");
            }
        };
    }

    @Override
    public void onError(int i, String s) {
        Log.d(Const.TAG, "feed load fail, errCode: " + i + ", errMsg: " + s);
    }

    @Override
    public void onFeedAdLoad(List<TTFeedAd> list) {
        if (list != null && list.size() > 0) {
            Log.d(Const.TAG, "feed load success");
            mTTFeedAd = list.get(0);
            showFeedAd();
        } else {
            Log.d(Const.TAG, "feed load success, but list is null");
        }
    }
}
