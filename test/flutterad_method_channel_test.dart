/*
 * @Author: lipeng 1162423147@qq.com
 * @Date: 2024-06-06 21:17:15
 * @LastEditors: lipeng 1162423147@qq.com
 * @LastEditTime: 2024-06-06 21:44:37
 * @FilePath: /flutterad/test/flutterad_method_channel_test.dart
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import 'package:flutter/services.dart';
import 'package:flutter_ad_gromore/flutterad_method_channel.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelFlutterad platform = MethodChannelFlutterad();
  const MethodChannel channel = MethodChannel('flutterad');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
