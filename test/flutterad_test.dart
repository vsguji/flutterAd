/*
 * @Author: lipeng 1162423147@qq.com
 * @Date: 2024-06-06 21:17:15
 * @LastEditors: lipeng 1162423147@qq.com
 * @LastEditTime: 2024-06-06 21:44:50
 * @FilePath: /flutterad/test/flutterad_test.dart
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import 'package:flutter_test/flutter_test.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutteradPlatform
    with MockPlatformInterfaceMixin
    implements FlutteradPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutteradPlatform initialPlatform = FlutteradPlatform.instance;

  test('$MethodChannelFlutterad is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterad>());
  });

  test('getPlatformVersion', () async {
    Flutterad flutteradPlugin = Flutterad();
    MockFlutteradPlatform fakePlatform = MockFlutteradPlatform();
    FlutteradPlatform.instance = fakePlatform;

    expect(await flutteradPlugin.getPlatformVersion(), '42');
  });
}
