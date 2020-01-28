package io.flutter.plugins.webviewflutter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Toast;

/**
 * Ugly implementation of file chooser
 */
// todo refine
public class FlutterWebViewFileChooser {
    private static final int REQUEST_SELECT_FILE = 101;

    private static Activity activity;
    private static ValueCallback<Uri[]> fileChosenCallback;

    public static void setActivity(Activity activity) {
        FlutterWebViewFileChooser.activity = activity;
    }

    public static boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_FILE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (fileChosenCallback != null) {
                fileChosenCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                fileChosenCallback = null;
                return true;
            }
        }
        return false;
    }

    static boolean onShowFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false;

        if (FlutterWebViewFileChooser.fileChosenCallback != null) {
            FlutterWebViewFileChooser.fileChosenCallback.onReceiveValue(null);
            FlutterWebViewFileChooser.fileChosenCallback = null;
        }

        FlutterWebViewFileChooser.fileChosenCallback = filePathCallback;

        Intent intent = fileChooserParams.createIntent();
        try {
            activity.startActivityForResult(intent, REQUEST_SELECT_FILE);
            return true;
        } catch (ActivityNotFoundException e) {
            FlutterWebViewFileChooser.fileChosenCallback = null;
            Toast.makeText(activity, "Cannot Open File Chooser", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
