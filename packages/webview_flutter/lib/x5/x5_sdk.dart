import 'dart:io';

import 'package:flutter/services.dart';

final MethodChannel _channel =
    Platform.isAndroid ? MethodChannel('codeshake.cn/x5_sdk') : null;

/// X5 WebView SDK controller.
class X5SDK {
  /// Get X5 controller instance.
  ///
  /// Note: May returns null if not in Android platform.
  factory X5SDK() {
    return Platform.isAndroid ? (_instance ??= X5SDK._()) : null;
  }

  X5SDK._();

  static X5SDK _instance;

  bool _hasInitializedSuccess;

  /// Initialize X5 environment.
  ///
  /// Returns whether it's initialized successfully.
  Future<bool> init() async {
    if (_hasInitializedSuccess ?? false) return true;

    return _channel
        .invokeMethod<bool>('initX5')
        .then<bool>((success) => _hasInitializedSuccess = success);
  }
}
