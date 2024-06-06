package com.pengsheng.flutterad.mediation.java;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.CSJSplashCloseType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.pengsheng.flutterad.R;
import com.pengsheng.flutterad.config.TTAdManagerHolder;
import com.pengsheng.flutterad.mediation.java.utils.Const;
import com.pengsheng.flutterad.utils.UIUtils;

/**
 * 融合demo，开屏广告使用示例。更多功能参考接入文档。
 *
 * 注意：每次加载的广告，只能展示一次
 *
 * 接入步骤：
 * 1、创建AdSlot对象
 * 2、创建TTAdNative对象
 * 3、创建加载、展示监听器
 * 4、加载广告
 * 5、加载并渲染成功后，展示广告
 * 6、在onDestroy中销毁广告
 */
public class MediationSplashActivity extends Activity {

    private FrameLayout mSplashContainer;


    private CSJSplashAd mCsjSplashAd;


    private TTAdNative.CSJSplashAdListener mCSJSplashAdListener;


    private CSJSplashAd.SplashAdListener mCSJSplashInteractionListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediation_activity_splash);
        mSplashContainer = findViewById(R.id.fl_content);

        // 加载并展示广告
        loadAndShowSplashAd();
    }

    private void loadAndShowSplashAd() {
        /** 1、创建AdSlot对象 */

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(getResources().getString(R.string.splash_media_id))
                .setImageAcceptedSize(UIUtils.getScreenWidthInPx(this),UIUtils.getScreenHeightInPx(this))
                .build();

        /** 2、创建TTAdNative对象 */

        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(this);

        /** 3、创建加载、展示监听器 */
        initListeners();

        /** 4、加载广告 */

        adNativeLoader.loadSplashAd(adSlot, mCSJSplashAdListener, 3500);
    }

    private void initListeners() {
        // 广告加载监听器

        this.mCSJSplashAdListener = new TTAdNative.CSJSplashAdListener() {
            @Override

            public void onSplashRenderSuccess(CSJSplashAd csjSplashAd) {
                /** 5、渲染成功后，展示广告 */
                Log.d(Const.TAG, "splash render success");
                mCsjSplashAd = csjSplashAd;
                csjSplashAd.setSplashAdListener(mCSJSplashInteractionListener);
                View splashView = csjSplashAd.getSplashView();
                UIUtils.removeFromParent(splashView);
                mSplashContainer.removeAllViews();
                mSplashContainer.addView(splashView);
            }

            public void onSplashLoadSuccess() {
                Log.d(Const.TAG, "splash load success");
            }

            @Override

            public void onSplashLoadSuccess(CSJSplashAd csjSplashAd) {

            }

            @Override

            public void onSplashLoadFail(CSJAdError csjAdError) {
                Log.d(Const.TAG, "splash load fail, errCode: " + csjAdError.getCode() + ", errMsg: " + csjAdError.getMsg());
            }

            @Override

            public void onSplashRenderFail(CSJSplashAd csjSplashAd, CSJAdError csjAdError) {
                Log.d(Const.TAG, "splash render fail, errCode: " + csjAdError.getCode() + ", errMsg: " + csjAdError.getMsg());
            }
        };
        // 广告展示监听器

        this.mCSJSplashInteractionListener = new CSJSplashAd.SplashAdListener() {
            @Override

            public void onSplashAdShow(CSJSplashAd csjSplashAd) {
                Log.d(Const.TAG, "splash show");
            }

            @Override

            public void onSplashAdClick(CSJSplashAd csjSplashAd) {
                Log.d(Const.TAG, "splash click");
            }

            @Override

            public void onSplashAdClose(CSJSplashAd csjSplashAd, int closeType) {

                if (closeType == CSJSplashCloseType.CLICK_SKIP) {
                    Log.d(Const.TAG, "开屏广告点击跳过");

                } else if (closeType == CSJSplashCloseType.COUNT_DOWN_OVER) {
                    Log.d(Const.TAG, "开屏广告点击倒计时结束");

                } else if (closeType == CSJSplashCloseType.CLICK_JUMP) {
                    Log.d(Const.TAG, "点击跳转");
                }
                finish();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** 6、在onDestroy中销毁广告 */
        if (mCsjSplashAd != null && mCsjSplashAd.getMediationManager() != null) {
            mCsjSplashAd.getMediationManager().destroy();
        }
    }
}
