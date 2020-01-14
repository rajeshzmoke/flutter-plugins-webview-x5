// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.webviewflutter;

import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

class FlutterCookieManager implements MethodCallHandler {
  private  static String formatExpiresString(long expires) {
    final DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    return format.format(new Date(expires));
  }

  private final MethodChannel methodChannel;

  FlutterCookieManager(BinaryMessenger messenger) {
    methodChannel = new MethodChannel(messenger, "plugins.flutter.io/cookie_manager");
    methodChannel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(MethodCall methodCall, Result result) {
    switch (methodCall.method) {
      case "setCookie": {
        setCookie(methodCall, result);
        break;
      }
      case "clearCookies":
        clearCookies(result);
        break;
      default:
        result.notImplemented();
    }
  }

  void dispose() {
    methodChannel.setMethodCallHandler(null);
  }

  private static void setCookie(MethodCall call, final MethodChannel.Result result) {
    String url = call.argument("url");
    String name = call.argument("name");
    String value = call.argument("value");
    String domain = call.argument("domain");
    String path = call.argument("path");
    Long expires = call.hasArgument("expires") ? call.<Long>argument("expires") : null;
    Integer maxAge = call.hasArgument("maxAge") ? call.<Integer>argument("maxAge") : null;
    Boolean isSecure = call.hasArgument("secure") ? call.<Boolean>argument("secure") : null;
    Boolean httpOnly = call.hasArgument("httpOnly") ? call.<Boolean>argument("httpOnly") : null;

    String cookieValue = name + "=" + value + "; Domain=" + domain + "; Path=" + path;
    if (expires != null) cookieValue += "; Expires=" + formatExpiresString(expires);
    if (maxAge != null) cookieValue += "; Max-Age=" + maxAge;
    if (isSecure != null && isSecure) cookieValue += "; Secure";
    if (httpOnly != null && httpOnly) cookieValue += "; HttpOnly";
    cookieValue += ";";

    final CookieManager cookieManager = CookieManager.getInstance();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      cookieManager.setCookie(url, cookieValue, new ValueCallback<Boolean>() {
        @Override
        public void onReceiveValue(Boolean success) {
          result.success(success);
        }
      });
    } else {
      cookieManager.setCookie(url, cookieValue);
      result.success(true);
    }
  }

  private static void clearCookies(final Result result) {
    CookieManager cookieManager = CookieManager.getInstance();
    final boolean hasCookies = cookieManager.hasCookies();
    if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      cookieManager.removeAllCookies(
          new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean value) {
              result.success(hasCookies);
            }
          });
    } else {
      cookieManager.removeAllCookie();
      result.success(hasCookies);
    }
  }
}
