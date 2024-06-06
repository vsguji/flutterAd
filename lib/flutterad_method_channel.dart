import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutterad_platform_interface.dart';

/// An implementation of [FlutteradPlatform] that uses method channels.
class MethodChannelFlutterad extends FlutteradPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutterad');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
