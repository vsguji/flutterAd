package com.pengsheng.flutterad.mediation.java;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.pengsheng.flutterad.R;
import com.pengsheng.flutterad.config.TTAdManagerHolder;
import com.pengsheng.flutterad.mediation.java.utils.Const;
import com.pengsheng.flutterad.utils.UIUtils;

import java.util.List;

/**
 * 融合demo，banner广告使用示例。更多功能参考接入文档。
 *
 * 注意：每次加载的广告，只能展示一次
 *
 * 接入步骤：
 * 1、创建AdSlot对象
 * 2、创建TTAdNative对象
 * 3、创建加载、展示监听器
 * 4、加载广告
 * 5、加载成功后，展示广告
 * 6、在onDestroy中销毁广告
 */
public class MediationBannerActivity extends Activity {

    public String mMediaId; // 融合广告位

    private TTNativeExpressAd mBannerAd; // Banner广告对象

    private TTAdNative.NativeExpressAdListener mBannerListener; // 广告加载监听器

    private TTNativeExpressAd.ExpressAdInteractionListener mBannerInteractionListener; // 广告展示监听器

    private TTAdDislike.DislikeInteractionCallback mDislikeCallback; // 接受广告关闭回调

    private FrameLayout mBannerContainer; // banner广告容器view

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediation_activity_banner);

        // 聚合广告位（在GroMore平台的广告位，注意不是adn的代码位）
        this.mMediaId = getResources().getString(R.string.banner_media_id);
        TextView tvMediationId = (TextView)this.findViewById(R.id.tv_media_id);
        tvMediationId.setText(getString(R.string.ad_mediation_id, this.mMediaId));

        // banner广告容器
        mBannerContainer = findViewById(R.id.banner_container);

        // 广告加载
        findViewById(R.id.bt_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBannerAd();
            }
        });

        // 广告展示
        findViewById(R.id.bt_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBannerAd();
            }
        });
    }

    private void loadBannerAd() {
        /** 1、创建AdSlot对象 */

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(this.mMediaId)
                .setImageAcceptedSize(UIUtils.dp2px(this, 320f), UIUtils.dp2px(this, 150f)) // 单位px
                .build();

        /** 2、创建TTAdNative对象 */

        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(this);

        /** 3、创建加载、展示监听器 */
        initListeners();

        /** 4、加载广告 */

        adNativeLoader.loadBannerExpressAd(adSlot, mBannerListener);
    }

    /**     5、广告加载成功后，设置监听器，展示广告 */
    private void showBannerAd() {
        if (mBannerAd != null) {
            mBannerAd.setExpressInteractionListener(mBannerInteractionListener);
            mBannerAd.setDislikeCallback(this, mDislikeCallback);
            mBannerAd.uploadDislikeEvent("mediation_dislike_event");
            /** 注意：使用融合功能时，load成功后可直接调用getExpressAdView获取广告view展示，而无需调用render等onRenderSuccess后 */

            View bannerView = mBannerAd.getExpressAdView();
            if (bannerView != null && mBannerContainer != null) {
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(bannerView);
            }
        } else {
            Log.d(Const.TAG, "请先加载广告或等待广告加载完毕后再展示广告");
        }
    }

    private void initListeners() {
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
                if (mBannerContainer != null)
                    mBannerContainer.removeAllViews();
                Log.d(Const.TAG, "banner closed");
            }

            @Override
            public void onCancel() { }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** 6、在onDestroy中销毁广告 */
        if (mBannerAd != null) {
            mBannerAd.destroy();
        }
    }
}
