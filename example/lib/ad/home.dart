import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_ad_gromore/event/ad_event.dart';
import 'package:flutter_ad_gromore/flutterad.dart';
import 'package:flutter_ad_gromore/view/ad_banner.dart';
import 'ads_config.dart';
import 'pro_page.dart';

/// 首页
class HomePage extends StatefulWidget {
  const HomePage({
    Key? key,
  }) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  String _result = '';
  String _adEvent = '';
  bool _isInited = false;

  late MethodChannel drawChannel;
  @override
  void initState() {
    setAdEvent();
    init().then((value) {
      if (value) {
        // showSplashAd(AdsConfig.logo);
        setState(() {
          _isInited = true;
        });
      }
    });
    drawChannel = const MethodChannel('flutterad_method');
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width;
    double height = MediaQuery.of(context).size.height;
    int value = width.toInt();
    log(value);
    return Scaffold(
      appBar: AppBar(
        title: const Text('Flutter GroMore Ads'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            const SizedBox(height: 10),
            Text('Result: $_result'),
            const SizedBox(height: 10),
            Text('onAdEvent: $_adEvent'),
            const SizedBox(height: 20),
            SizedBox(
              width: double.maxFinite,
              child: ElevatedButton(
                child: const Text('初始化'),
                onPressed: () {
                  init();
                },
              ),
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Flexible(
                  child: ElevatedButton(
                    child: const Text('请求应用跟踪授权'),
                    onPressed: () {
                      requestIDFA();
                    },
                  ),
                ),
                const SizedBox(height: 20),
                Flexible(
                  child: ElevatedButton(
                    child: const Text('动态请求相关权限'),
                    onPressed: () {
                      requestPermissionIfNecessary();
                    },
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                ElevatedButton(
                  child: const Text('开屏广告(Logo)'),
                  onPressed: () {
                    showSplashAd(AdsConfig.logo2);
                  },
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  child: const Text('开屏广告(全屏)'),
                  onPressed: () {
                    showSplashAd();
                  },
                ),
              ],
            ),
            const SizedBox(height: 20),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                ElevatedButton(
                  child: const Text('插竖屏'),
                  onPressed: () {
                    showInterstitialAd(AdsConfig.interstitialId);
                  },
                ),
                const SizedBox(height: 10),
                ElevatedButton(
                  child: const Text('插横屏'),
                  onPressed: () {
                    showInterstitialAd(AdsConfig.interstitialIdHorizontal);
                  },
                ),
                const SizedBox(height: 10),
                ElevatedButton(
                  child: const Text('插半屏'),
                  onPressed: () {
                    showInterstitialAd(AdsConfig.interstitialIdHalf);
                  },
                ),
              ],
            ),
            const SizedBox(height: 20),
            const Center(
              child: Text(
                '🏆 Pro 版付费功能 🏆',
                style: TextStyle(
                  color: Colors.purple,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            const SizedBox(height: 10),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                ElevatedButton(
                  child: const Text('信息流广告(Pro)'),
                  onPressed: () {
                    Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const ProPage(),
                        ));
                  },
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  child: const Text('激励视频广告(Pro)'),
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const ProPage(),
                      ),
                    );
                  },
                ),
              ],
            ),
            if (_isInited)
              Row(
                children: [
                  Expanded(
                      child: SizedBox.fromSize(
                    size: Size(width, height),
                    child: AndroidView(
                      viewType: 'flutterad_banner',
                      creationParams: <String, dynamic>{
                        "posId": AdsConfig.bannerId,
                        "width": 300,
                        "height": 250,
                      },
                      creationParamsCodec: const StandardMessageCodec(),
                    ),
                  ))
                ],
              ),
            TextButton(
                onPressed: () async {
                  var isOk = await Flutterad.showInterstitialAd(
                      AdsConfig.interstitialId);
                  if (isOk) {}
                },
                child: const Text('showInterstitialAd')),
            TextButton(
                onPressed: () async {
                  var isOk =
                      await Flutterad.showRewardAd(AdsConfig.rewardVideoId);
                  if (isOk) {}
                },
                child: const Text('showRewardVideoAd'))
          ],
        ),
      ),
      // bottomNavigationBar: SafeArea(
      //   child: Container(
      //       width: double.maxFinite,
      //       height: 140,
      //       color: Colors.grey[200],
      //       alignment: Alignment.center,
      //       child: Column(
      //         children: [
      //           const SizedBox(height: 10),
      //           const Center(child: Text('👇🏻 Banner 广告 👇🏻')),
      //           const SizedBox(height: 10),
      //           AdBannerWidget(
      //             posId: AdsConfig.bannerId,
      //             width: 300,
      //             height: 75,
      //           ),
      //         ],
      //       )),
      // ),
    );
  }

  /// 初始化广告 SDK
  Future<bool> init() async {
    try {
      bool result = await Flutterad.initAd(
        AdsConfig.appId,
        config: AdsConfig.config,
        limitPersonalAds: 1,
      );
      _result = "广告SDK 初始化${result ? '成功' : '失败'}";
      setState(() {
        _isInited = true;
      });
      return result;
    } on PlatformException catch (e) {
      _result =
          "广告SDK 初始化失败 code:${e.code} msg:${e.message} details:${e.details}";
    }
    setState(() {
      _isInited = false;
    });
    return false;
  }

  /// 设置广告监听
  Future<void> setAdEvent() async {
    setState(() {
      _adEvent = '设置成功';
    });
    Flutterad.onEventListener((event) {
      _adEvent = 'adId:${event.adId} action:${event.action}';
      if (event is AdErrorEvent) {
        // 错误事件
        _adEvent += ' errCode:${event.errCode} errMsg:${event.errMsg}';
      } else if (event is AdRewardEvent) {
        // 激励事件
        _adEvent +=
            ' rewardVerify:${event.rewardVerify} rewardAmount:${event.rewardAmount} rewardName:${event.rewardName} errCode:${event.errCode} errMsg:${event.errMsg} customData:${event.customData} userId:${event.userId}';
      } else {
        if (AdsConfig.splashId == event.adId) {
          switch (event.action) {
            case AdEventAction.onAdLoaded:
              //
              break;
            case AdEventAction.onAdClosed:
              //
              break;
            case AdEventAction.onAdClicked:
              //
              break;
            default:
          }
        }
      }
      // // 测试关闭 Banner（会员场景）
      // if (event.action == AdEventAction.onAdClosed &&
      //     event.adId == AdsConfig.bannerId02) {
      //   _adEvent += '仅会员可以关闭广告';
      // }
      debugPrint('onEventListener:$_adEvent');
      setState(() {});
    });
  }

  /// 请求应用跟踪透明度授权
  Future<void> requestIDFA() async {
    bool result = await Flutterad.requestIDFA;
    _adEvent = '请求广告标识符:$result';
    setState(() {});
  }

  /// 请求应用跟踪透明度授权
  Future<void> requestPermissionIfNecessary() async {
    bool result = await Flutterad.requestPermissionIfNecessary;
    _adEvent = '请求相关权限:$result';
    setState(() {});
  }

  /// 展示开屏广告
  /// [logo] 展示如果传递则展示logo，不传递不展示
  Future<void> showSplashAd([String? logo]) async {
    try {
      bool result = await Flutterad.showSplashAd(
        AdsConfig.splashId,
        logo: logo,
        timeout: 3.5,
      );
      _result = "展示开屏广告${result ? '成功' : '失败'}";
    } on PlatformException catch (e) {
      _result = "展示开屏广告失败 code:${e.code} msg:${e.message} details:${e.details}";
    }
  }

  /// 展示插屏广告
  Future<void> showInterstitialAd(String posid) async {
    try {
      bool result = await Flutterad.showInterstitialAd(posid);
      _result = "展示插屏广告${result ? '成功' : '失败'}";
    } on PlatformException catch (e) {
      _result = "展示插屏广告失败 code:${e.code} msg:${e.message} details:${e.details}";
    }
    setState(() {});
  }
}
