package io.flutter.plugins.webviewflutter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

class AutoPauseUtil {
    static void setupAutoPause(Context context, final InputAwareWebView webView) {
        context = context.getApplicationContext();
        if (!(context instanceof Application)) return;
        final Application application = (Application) context;

        final Activity webViewActivity = WebViewFlutterPlugin.activityRef.get();
        if (webViewActivity == null) return;

        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (webViewActivity == activity) webView.onResume();
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (webViewActivity == activity) webView.onPause();
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (webViewActivity == activity) application.unregisterActivityLifecycleCallbacks(this);
            }
        });
    }
}
