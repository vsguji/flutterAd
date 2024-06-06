package com.pengsheng.flutterad.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pengsheng.flutterad.PluginDelegate;
import com.pengsheng.flutterad.page.AdBannerView;
import com.pengsheng.flutterad.view.AdFeedView;
import com.pengsheng.flutterad.view.AdSplashView;

import java.util.Map;

import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

/**
 * 原生平台 View 工厂
 */
public class NativeViewFactory extends PlatformViewFactory {
    @NonNull
    private final String viewName;// View 名字
    private final PluginDelegate pluginDelegate; // 插件代理类

    public NativeViewFactory(String viewName, @NonNull PluginDelegate pluginDelegate) {
        super(StandardMessageCodec.INSTANCE);
        this.viewName = viewName;
        this.pluginDelegate = pluginDelegate;
    }

    @NonNull
    @Override
    public PlatformView create(@NonNull Context context, int id, @Nullable Object args) {
        final Map<String, Object> creationParams = (Map<String, Object>) args;
        if (this.viewName.equals(PluginDelegate.KEY_BANNER_VIEW)) {
            return new AdBannerView(context, id, creationParams, pluginDelegate);
        } else if (this.viewName.equals(PluginDelegate.KEY_SPLASH_VIEW)) {
            return new AdSplashView(context,id,creationParams,pluginDelegate);
        } else if (this.viewName.equals(PluginDelegate.KEY_Feed_View)) {
            return new AdFeedView(context,id,creationParams,pluginDelegate);
        }
        return null;
    }
}