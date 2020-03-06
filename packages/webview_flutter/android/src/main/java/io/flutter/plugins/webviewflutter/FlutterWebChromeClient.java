package io.flutter.plugins.webviewflutter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

class FlutterWebChromeClient extends WebChromeClient {

    private static final String TAG = "FlutterWebChromeClient";
    private final MethodChannel methodChannel;
    private boolean isFullscreen = false;
    private CustomViewCallback customViewCallback;
    private View customView;
    private ViewGroup rootView;

    FlutterWebChromeClient(MethodChannel methodChannel, ViewGroup rootView) {
        this.methodChannel = methodChannel;
        this.rootView = rootView;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }
        customView = view;
        rootView.addView(customView);
        customViewCallback = callback;
        Activity activity = WebViewFlutterPlugin.activityRef.get();
        if (activity != null) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        isFullscreen = true;
        onScreenOrientationChanged(true);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onHideCustomView() {
        if (customView == null) return;
        rootView.removeView(customView);
        customViewCallback.onCustomViewHidden();
        customView = null;
        Activity activity = WebViewFlutterPlugin.activityRef.get();
        if (activity != null) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        isFullscreen = false;
        onScreenOrientationChanged(false);
        super.onHideCustomView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequest(PermissionRequest request) {
        request.grant(request.getResources());
    }

    boolean exitFullscreen() {
        if (isFullscreen) {
            onHideCustomView();
            return true;
        }
        return false;
    }

    private void onScreenOrientationChanged(boolean isFullscreen) {
        Map<String, Object> args = new HashMap<>();
        args.put("isLandscape", isFullscreen);
        methodChannel.invokeMethod("onScreenOrientationChanged", args);
    }
}
