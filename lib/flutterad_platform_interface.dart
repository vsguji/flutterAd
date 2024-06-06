import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutterad_method_channel.dart';

abstract class FlutteradPlatform extends PlatformInterface {
  /// Constructs a FlutteradPlatform.
  FlutteradPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutteradPlatform _instance = MethodChannelFlutterad();

  /// The default instance of [FlutteradPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterad].
  static FlutteradPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutteradPlatform] when
  /// they register themselves.
  static set instance(FlutteradPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
