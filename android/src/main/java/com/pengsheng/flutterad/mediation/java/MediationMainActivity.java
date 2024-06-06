package com.pengsheng.flutterad.mediation.java;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.mtesttools.api.TTMediationTestTool;
import com.pengsheng.flutterad.R;
import com.pengsheng.flutterad.config.TTAdManagerHolder;

public class MediationMainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediation_activity_main);

        TextView tvVersion = findViewById(R.id.tv_version);
        String ver = getString(R.string.main_sdk_version_tip, TTAdManagerHolder.get().getSDKVersion());
        tvVersion.setText(ver);

        findViewById(R.id.btn_main_feed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediationMainActivity.this, MediationFeedActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_feed_listview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediationMainActivity.this, MediationFeedListViewActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_main_feed_recyclerview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediationMainActivity.this, MediationFeedRecyclerViewActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_main_draw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediationMainActivity.this, MediationDrawActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediationMainActivity.this, MediationBannerActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_main_Splash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediationMainActivity.this, MediationSplashStartActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_Reward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediationMainActivity.this, MediationRewardActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_full_interaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediationMainActivity.this, MediationInterstitialFullActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_tool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTMediationTestTool.launchTestTools(MediationMainActivity.this, new TTMediationTestTool.ImageCallBack() {
                    @Override
                    public void loadImage(ImageView imageView, String s) {

                    }
                });
            }
        });
    }
}
