package io.flutter.plugins.webviewflutter;

import android.net.Uri;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.LinkedList;
import java.util.List;

public class MultiWebViewChromeClient extends WebChromeClient {
    private List<WebChromeClient> list = new LinkedList<>();

    void addSubClient(WebChromeClient client) {
        list.add(client);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (WebChromeClient client : list) {
                if (client.onShowFileChooser(webView, filePathCallback, fileChooserParams)) return true;
            }
        }
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        for (WebChromeClient client : list) {
            client.onProgressChanged(view, newProgress);
        }
        super.onProgressChanged(view, newProgress);
    }

}
