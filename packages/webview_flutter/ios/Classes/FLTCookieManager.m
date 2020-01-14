// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#import "FLTCookieManager.h"

@implementation FLTCookieManager {
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar> *)registrar {
  FLTCookieManager *instance = [[FLTCookieManager alloc] init];

  FlutterMethodChannel *channel =
      [FlutterMethodChannel methodChannelWithName:@"plugins.flutter.io/cookie_manager"
                                  binaryMessenger:[registrar messenger]];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall *)call result:(FlutterResult)result {
  if ([[call method] isEqualToString:@"setCookie"]) {
    [self setCookie:call result:result];
  } else if ([[call method] isEqualToString:@"clearCookies"]) {
    [self clearCookies:result];
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (void)setCookie:(FlutterMethodCall *)call result: (FlutterResult)result {
  if (@available(iOS 11.0, *)) {
    NSString *url = call.arguments[@"url"];
    NSString *name = call.arguments[@"name"];
    NSString *value = call.arguments[@"value"];
    NSString *domain = call.arguments[@"domain"];
    NSString *path = call.arguments[@"path"];
    NSInteger expires = ((NSNumber *) call.arguments[@"expires"]).longValue / 1000;
    NSInteger maxAge = ((NSNumber *) call.arguments[@"maxAge"]).intValue;
    bool secure = (bool) call.arguments[@"secure"];
    bool httpOnly = (bool) call.arguments[@"httpOnly"];

    WKHTTPCookieStore *cookieStore = [[WKWebsiteDataStore defaultDataStore] httpCookieStore];
    NSMutableDictionary<NSHTTPCookiePropertyKey,id> *properties = [[NSMutableDictionary alloc] initWithDictionary: @{
      NSHTTPCookieOriginURL: url,
      NSHTTPCookieName: name,
      NSHTTPCookieValue: value,
      NSHTTPCookieDomain: domain,
      NSHTTPCookiePath: path,
    }];
    if (expires != 0) [properties setValue:[NSDate dateWithTimeIntervalSince1970: expires] forKey: NSHTTPCookieExpires];
    if (maxAge != 0) [properties setValue:@(maxAge) forKey: NSHTTPCookieMaximumAge];
    if (secure) [properties setValue:@(YES) forKey: NSHTTPCookieSecure];
    if (httpOnly) [properties setValue:@(YES) forKey:@"HttpOnly"];

    NSHTTPCookie *cookie = [NSHTTPCookie cookieWithProperties: properties];
    [cookieStore setCookie:cookie completionHandler:^() {
      result(@(YES));
    }];
  } else {
    NSLog(@"Setting cookies is not supported for Flutter WebViews prior to iOS 11.");
    result(@(NO));
  }
}

- (void)clearCookies:(FlutterResult)result {
  if (@available(iOS 9.0, *)) {
    NSSet<NSString *> *websiteDataTypes = [NSSet setWithObject:WKWebsiteDataTypeCookies];
    WKWebsiteDataStore *dataStore = [WKWebsiteDataStore defaultDataStore];

    void (^deleteAndNotify)(NSArray<WKWebsiteDataRecord *> *) =
        ^(NSArray<WKWebsiteDataRecord *> *cookies) {
          BOOL hasCookies = cookies.count > 0;
          [dataStore removeDataOfTypes:websiteDataTypes
                        forDataRecords:cookies
                     completionHandler:^{
                       result(@(hasCookies));
                     }];
        };

    [dataStore fetchDataRecordsOfTypes:websiteDataTypes completionHandler:deleteAndNotify];
  } else {
    // support for iOS8 tracked in https://github.com/flutter/flutter/issues/27624.
    NSLog(@"Clearing cookies is not supported for Flutter WebViews prior to iOS 9.");
  }
}

@end
