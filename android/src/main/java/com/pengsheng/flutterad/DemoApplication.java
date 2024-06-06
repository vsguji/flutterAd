package com.pengsheng.flutterad;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

/**
 *
 */
public class DemoApplication extends MultiDexApplication {

    public static  String process_name_ue = "process_name_ue";

    private  static Context context;

    @Override
    protected  void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        DemoApplication.context = this;
    }

    @Override
    public  void onCreate() {
        super.onCreate();
       // MultiDex.install(this);
       // DemoApplication.context = this;
    }

    public  static  Context getAppContext() {
        return DemoApplication.context;
    }
}
