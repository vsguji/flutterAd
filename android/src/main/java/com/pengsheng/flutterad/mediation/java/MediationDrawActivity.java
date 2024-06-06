package com.pengsheng.flutterad.mediation.java;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTDrawFeedAd;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.mediation.ad.MediationExpressRenderListener;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationNativeManager;
import com.pengsheng.flutterad.R;
import com.pengsheng.flutterad.config.TTAdManagerHolder;
import com.pengsheng.flutterad.mediation.java.utils.Const;
import com.pengsheng.flutterad.mediation.java.utils.FeedAdUtils;
import com.pengsheng.flutterad.utils.UIUtils;

import java.util.List;

/**
 * 融合demo，Draw流广告使用示例（模板和自渲染）。更多功能参考接入文档。
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
public class MediationDrawActivity extends Activity {

    public String mMediaId; // 融合广告位

    private TTFeedAd mTTFeedAd; // Feed广告对象

    private TTAdNative.DrawFeedAdListener mDrawFeedAdListener; // 广告加载监听器
    private MediationExpressRenderListener mExpressAdInteractionListener; // 模板广告展示监听器

    private TTNativeAd.AdInteractionListener mAdInteractionListener; // 自渲染广告展示监听器

    private FrameLayout mFeedContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediation_activity_feed);

        // 聚合广告位（在GroMore平台的广告位，注意不是adn的代码位）
        mMediaId = getResources().getString(R.string.draw_express_media_id);
        TextView tvMediationId = findViewById(R.id.tv_media_id);
        tvMediationId.setText(String.format(getResources().getString(R.string.ad_mediation_id), mMediaId));

        // feed流广告容器
        mFeedContainer = findViewById(R.id.fl_content);

        // 广告加载
        findViewById(R.id.bt_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFeedAd();
            }
        });

        // 广告展示
        findViewById(R.id.bt_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedAd();
            }
        });
    }

    private void loadFeedAd() {
        /** 1、创建AdSlot对象 */

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(this.mMediaId)
                .setImageAcceptedSize(UIUtils.getScreenWidthInPx(this), UIUtils.dp2px(this, 340)) // 单位px
                .setAdCount(1) // 请求广告数量为1到3条 （优先采用平台配置的数量）
                .build();

        /** 2、创建TTAdNative对象 */

        TTAdNative adNativeLoader = TTAdManagerHolder.get().createAdNative(this);

        /** 3、创建加载、展示监听器 */
        initListeners();

        /** 4、加载广告 */

        adNativeLoader.loadDrawFeedAd(adSlot, mDrawFeedAdListener);
    }

    // 广告加载成功后，展示广告
    private void showFeedAd() {
        if (this.mTTFeedAd == null) {
            Log.i(Const.TAG, "请先加载广告或等待广告加载完毕后再调用show方法");
            return;
        }
        /** 5、展示广告 */
        MediationNativeManager manager = mTTFeedAd.getMediationManager();
        if (manager != null) {
            if (manager.isExpress()) { // --- 模板Draw广告
                mTTFeedAd.setExpressRenderListener(mExpressAdInteractionListener);
                mTTFeedAd.render(); // 调用render方法进行渲染，在onRenderSuccess中展示广告

            } else {                   // --- 自渲染Draw广告

                // 自渲染广告返回的是广告素材，开发者自己将其渲染成view
                View feedView = FeedAdUtils.getFeedAdFromFeedInfo(mTTFeedAd, this, null, mAdInteractionListener);
                if (feedView != null) {
                    UIUtils.removeFromParent(feedView);
                    mFeedContainer.removeAllViews();
                    mFeedContainer.addView(feedView);
                }
            }
        }
    }

    private void initListeners() {
        // 广告加载监听器

        this.mDrawFeedAdListener = new TTAdNative.DrawFeedAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.d(Const.TAG, "draw load fail, errCode: " + i + ", errMsg: " + s);
            }

            @Override

            public void onDrawFeedAdLoad(List<TTDrawFeedAd> list) {
                if (list != null && list.size() > 0) {
                    Log.d(Const.TAG, "draw load success");
                    mTTFeedAd = list.get(0);
                } else {
                    Log.d(Const.TAG, "draw load success, but list is null");
                }
            }
        };
        // 模板广告展示监听器
        this.mExpressAdInteractionListener = new MediationExpressRenderListener() {
            @Override
            public void onRenderFail(View view, String s, int i) {
                Log.d(Const.TAG, "draw express render fail, errCode: " + i + ", errMsg: " + s);
            }

            @Override
            public void onAdClick() {
                Log.d(Const.TAG, "draw express click");
            }

            @Override
            public void onAdShow() {
                Log.d(Const.TAG, "draw express show");
            }

            @Override
            public void onRenderSuccess(View view, float v, float v1, boolean b) {
                Log.d(Const.TAG, "draw express render success");
                if (mTTFeedAd != null) {
                    View expressFeedView = mTTFeedAd.getAdView(); // *** 注意不要使用onRenderSuccess参数中的view ***
                    UIUtils.removeFromParent(expressFeedView);
                    mFeedContainer.removeAllViews();
                    mFeedContainer.addView(expressFeedView);
                }
            }
        };
        // 自渲染广告展示监听器

        this.mAdInteractionListener = new TTNativeAd.AdInteractionListener() {
            @Override

            public void onAdClicked(View view, TTNativeAd ttNativeAd) {
                Log.d(Const.TAG, "draw click");
            }

            @Override

            public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
                Log.d(Const.TAG, "draw creative click");
            }

            @Override

            public void onAdShow(TTNativeAd ttNativeAd) {
                Log.d(Const.TAG, "draw show");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** 6、在onDestroy中销毁广告 */
        if (mTTFeedAd != null) {
            mTTFeedAd.destroy();
        }
    }
}
