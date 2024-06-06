package com.pengsheng.flutterad.mediation.java;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.pengsheng.flutterad.R;
import  com.pengsheng.flutterad.mediation.java.MediationSplashActivity;

public class MediationSplashStartActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediation_activity_splash_start);

        // 聚合广告位（在GroMore平台的广告位，注意不是adn的代码位）
        String mediaId = getResources().getString(R.string.splash_media_id);
        TextView tvMediationId = findViewById(R.id.tv_media_id);
        tvMediationId.setText(String.format(getResources().getString(R.string.ad_mediation_id), mediaId));

        // 启动开屏广告Activity
        findViewById(R.id.bt_load_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediationSplashStartActivity.this, MediationSplashActivity.class);
                startActivity(intent);
            }
        });
    }
}
