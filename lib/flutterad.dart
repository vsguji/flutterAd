/*
 * @Author: lipeng 1162423147@qq.com
 * @Date: 2024-06-01 09:36:40
 * @LastEditors: lipeng 1162423147@qq.com
 * @LastEditTime: 2024-06-06 16:59:13
 * @FilePath: /flutterad/lib/flutterad.dart
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import 'dart:io';

import 'package:flutter/services.dart';

import 'flutterad_platform_interface.dart';
import 'view/ad_banner.dart';
import 'event/ad_error_event.dart';
import 'event/ad_event.dart';
import 'event/ad_event_action.dart';
import 'event/ad_event_handler.dart';
import 'event/ad_reward_event.dart';

class Flutterad {
  Future<String?> getPlatformVersion() {
    return FlutteradPlatform.instance.getPlatformVersion();
  }

  // 方法通道
  static const MethodChannel _methodChannel = MethodChannel('flutterad_method');
  // 事件通道
  static const EventChannel _eventChannel = EventChannel('flutterad');

  ///事件回调
  ///@params onData 事件回调
  static Future<void> onEventListener(
      OnAdEventListener onAdEventListener) async {
    _eventChannel.receiveBroadcastStream().listen((data) {
      hanleAdEvent(data, onAdEventListener);
    });
  }

  /// 请求应用跟踪透明度授权(仅 iOS)
  static Future<bool> get requestIDFA async {
    if (Platform.isIOS) {
      final bool result = await _methodChannel.invokeMethod('requestIDFA');
      return result;
    }
    return true;
  }

  /// 动态请求相关权限（仅 Android）
  static Future<bool> get requestPermissionIfNecessary async {
    if (Platform.isAndroid) {
      final bool result =
          await _methodChannel.invokeMethod('requestPermissionIfNecessary');
      return result;
    }
    return true;
  }

  /// 初始化广告
  /// [appId] 应用ID
  /// [config] 配置文件名称
  /// [limitPersonalAds] 是否限制个性化广告，0：不限制 1：限制
  static Future<bool> initAd(String appId,
      {String? config, int limitPersonalAds = 0}) async {
    final bool result = await _methodChannel.invokeMethod(
      'initAd',
      {
        'appId': appId,
        'config': config,
        'limitPersonalAds': limitPersonalAds,
      },
    );
    return result;
  }

  /// 展示开屏广告
  /// [posId] 广告位 id
  /// [logo] 如果传值则展示底部logo，不传不展示，则全屏展示
  /// [timeout] 加载超时时间
  static Future<bool> showSplashAd(String posId,
      {String? logo, double timeout = 3.5}) async {
    final bool result = await _methodChannel.invokeMethod(
      'showSplashAd',
      {
        'posId': posId,
        'logo': logo,
        'timeout': timeout,
      },
    );
    return result;
  }

  /// 展示插屏广告
  /// [posId] 广告位 id
  static Future<bool> showInterstitialAd(String posId) async {
    final bool result = await _methodChannel.invokeMethod(
      'showInterstitialAd',
      {'posId': posId},
    );
    return result;
  }

  /// 展示激励广告
  static Future<bool> showRewardAd(String posId) async {
    final bool result = await _methodChannel.invokeMethod('showRewardVideoAd', {
      'posId': posId,
    });
    return result;
  }
}
