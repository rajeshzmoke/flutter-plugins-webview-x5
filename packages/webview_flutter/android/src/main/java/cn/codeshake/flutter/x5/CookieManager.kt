package cn.codeshake.flutter.x5

import android.annotation.TargetApi
import android.os.Build

/**
 * Cookie manager wrapper to support both platform and X5 WebView.
 */
object CookieManager {
    private val platformManager: android.webkit.CookieManager = android.webkit.CookieManager.getInstance()
    private val x5Manager: com.tencent.smtt.sdk.CookieManager = com.tencent.smtt.sdk.CookieManager.getInstance()

    private var hasX5Initialized: Boolean = false

    fun onX5Initialized() {
        hasX5Initialized = true
    }

    fun hasCookies(): Boolean {
        return if (hasX5Initialized) x5Manager.hasCookies() else platformManager.hasCookies()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun removeAllCookies(callback: ValueCallback<Boolean>?) {
        x5Manager.removeAllCookies(if (hasX5Initialized) callback else null)
        platformManager.removeAllCookies(if (hasX5Initialized) null else callback)
    }

    fun removeAllCookie() {
        x5Manager.removeAllCookie()
        platformManager.removeAllCookie()
    }
}

interface ValueCallback<T> : android.webkit.ValueCallback<T>, com.tencent.smtt.sdk.ValueCallback<T>

