package cn.codeshake.flutter.x5

import android.content.Context
import com.tencent.smtt.sdk.QbSdk
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/**
 * Control X5 things.
 */
class X5SDK(private val context: Context, messenger: BinaryMessenger) : MethodChannel.MethodCallHandler {
    private var channel: MethodChannel = MethodChannel(messenger, "codeshake.cn/x5_sdk")

    init {
        channel.setMethodCallHandler(this)
    }

    fun dispose() {
        channel.setMethodCallHandler(null)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "initX5" -> initEnvironment(result)
            else -> result.notImplemented()
        }
    }

    private fun initEnvironment(result: MethodChannel.Result) {
        QbSdk.initX5Environment(context.applicationContext, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
            }

            override fun onViewInitFinished(success: Boolean) {
                result.success(success)
            }
        })
    }
}
