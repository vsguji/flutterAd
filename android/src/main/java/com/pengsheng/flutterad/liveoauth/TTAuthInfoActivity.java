package com.pengsheng.flutterad.liveoauth;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.pengsheng.flutterad.R;
import com.pengsheng.flutterad.liveoauth.TTLiveTokenHelper;
import com.pengsheng.flutterad.liveoauth.TTInjectionAuthImpl;

/**
 * Create by WUzejian on 2022/2/25.
 * 模拟宿主接入抖音授权SDK
 */
public class TTAuthInfoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        RadioGroup radioGroup = findViewById(R.id.radio_group_layout);
        final RadioButton radioButton1 = findViewById(R.id.douyin_account_btn);
        final RadioButton radioButton2 = findViewById(R.id.no_auth_provided);
        radioButton2.setChecked(!TTLiveTokenHelper.getInstance().useHostAuth());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.douyin_account_btn) {
                    TTLiveTokenHelper.getInstance().setUseHostAuth(true);

                    TTAdSdk.updateConfigAuth(new TTAdConfig.Builder().injectionAuth(new TTInjectionAuthImpl()).build());
                    Toast.makeText(TTAuthInfoActivity.this, "设置成功，冷启动后生效", Toast.LENGTH_LONG).show();
                    radioButton1.setChecked(true);
                    radioButton2.setChecked(false);
                } else if (checkedId == R.id.no_auth_provided) {
                    TTLiveTokenHelper.getInstance().setUseHostAuth(false);

                    TTAdSdk.updateConfigAuth(new TTAdConfig.Builder().build());
                    Toast.makeText(TTAuthInfoActivity.this, "设置成功，冷启动后生效", Toast.LENGTH_LONG).show();
                    radioButton1.setChecked(false);
                    radioButton2.setChecked(true);
                }
            }
        });

        findViewById(R.id.clear_token).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTLiveTokenHelper.getInstance().clearToken();
                Toast.makeText(TTAuthInfoActivity.this,"清除成功",Toast.LENGTH_LONG).show();
            }
        });
    }


}
