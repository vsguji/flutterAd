import 'package:flutter_test/flutter_test.dart';
import 'package:flutterad/flutterad.dart';
import 'package:flutterad/flutterad_platform_interface.dart';
import 'package:flutterad/flutterad_method_channel.dart';
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
