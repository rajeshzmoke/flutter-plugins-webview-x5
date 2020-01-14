package cn.codeshake.flutter.x5

import android.annotation.TargetApi
import android.os.Build
import android.util.Pair
import androidx.annotation.RequiresApi
import java.util.*

/**
 * Cookie manager wrapper to support both platform and X5 WebView.
 */
object CookieManager {
    private val platformManager: android.webkit.CookieManager = android.webkit.CookieManager.getInstance()
    private val x5Manager: com.tencent.smtt.sdk.CookieManager = com.tencent.smtt.sdk.CookieManager.getInstance()

    private var hasX5Initialized: Boolean = false
    private val pendingCookiesToSet: MutableList<Pair<String, String>> = LinkedList()

    fun onX5Initialized() {
        hasX5Initialized = true

        if (hasX5Initialized) {
            for (pair in pendingCookiesToSet) {
                x5Manager.setCookie(pair.first, pair.second);
            }
            pendingCookiesToSet.clear()
        }
    }

    fun hasCookies(): Boolean {
        return if (hasX5Initialized) x5Manager.hasCookies() else platformManager.hasCookies()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setCookie(url: String, value: String, callback: ValueCallback<Boolean>) {
        if (hasX5Initialized) {
            platformManager.setCookie(url, value)
            x5Manager.setCookie(url, value, callback)
        } else {
            platformManager.setCookie(url, value, callback)
            // set after initialized, otherwise  X5 WebView would not get the cookies
            pendingCookiesToSet.add(Pair(url, value))
        }
    }

    fun setCookie(url: String, value: String) {
        platformManager.setCookie(url, value)
        if (hasX5Initialized) {
            x5Manager.setCookie(url, value)
        } else {
            // set after initialized, otherwise  X5 WebView would not get the cookies
            pendingCookiesToSet.add(Pair(url, value))
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun removeAllCookies(callback: ValueCallback<Boolean>?) {
        pendingCookiesToSet.clear()
        x5Manager.removeAllCookies(if (hasX5Initialized) callback else null)
        platformManager.removeAllCookies(if (hasX5Initialized) null else callback)
    }

    fun removeAllCookie() {
        pendingCookiesToSet.clear()
        x5Manager.removeAllCookie()
        platformManager.removeAllCookie()
    }
}

interface ValueCallback<T> : android.webkit.ValueCallback<T>, com.tencent.smtt.sdk.ValueCallback<T>

