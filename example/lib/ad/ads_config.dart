/*
 * @Author: lipeng 1162423147@qq.com
 * @Date: 2024-06-01 17:56:11
 * @LastEditors: lipeng 1162423147@qq.com
 * @LastEditTime: 2024-06-06 10:55:08
 * @FilePath: /flutterad/lib/ad/ads_config.dart
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import 'dart:io';

/// 广告配置信息
class AdsConfig {
  /// 获取 logo 资源名称
  static String get logo {
    if (Platform.isAndroid) {
      return 'flutterads_logo';
    } else {
      return 'LaunchImage';
    }
  }

  /// 获取 logo 资源名称2
  static String get logo2 {
    if (Platform.isAndroid) {
      return 'flutterads_logo2';
    } else {
      return 'LaunchImage2';
    }
  }

  /// 获取开屏广告位id
  static String get appId {
    return '5551662';
  }

  /// 获取 App config
  static String get config {
    if (Platform.isIOS) {
      return 'ios_config_5209496';
    }
    return 'android_config_5216573.json';
  }

  /// 获取开屏广告位id
  static String get splashId {
    return '102914843';
  }

  /// 获取插屏广告位id 竖屏
  static String get interstitialId => '102918147';

  /// 获取插屏广告位id 横屏
  static String get interstitialIdHorizontal => '102918149';

  /// 获取插屏广告位id 半屏
  static String get interstitialIdHalf => '102919060';

  /// 获取激励视频广告位id
  static String get rewardVideoId => '102919164';

  /// 获取 Banner 广告位id
  static String get bannerId => '102914453';

  /// 获取 Feed 信息流广告位id
  static String get feedId => '102914555';

  /// 获取draw 信息流广告位 id
  static String get drawFeedId => '102919631';
}
