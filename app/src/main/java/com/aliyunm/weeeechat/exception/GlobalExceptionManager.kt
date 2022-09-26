package com.aliyunm.weeeechat.exception

import android.os.Looper
import android.util.Log
import com.google.gson.stream.MalformedJsonException
import java.net.UnknownHostException

/**
 * 全局异常管理器
 */
object GlobalExceptionManager : Thread.UncaughtExceptionHandler {
    private const val TAG = "GlobalExceptionManager"

    private val mDefaultCaughtExceptionHandler : Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()!!

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     *
     * Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     * @param t the thread
     * @param e the exception
     */
    override fun uncaughtException(t : Thread, e : Throwable) {
        if (!handleException(e)) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultCaughtExceptionHandler.uncaughtException(t, e)
        } else {
            e.printStackTrace()
        }
    }

    private fun handleException(ex : Throwable) : Boolean {
        val msg = when(ex) {
            is NullPointerException -> {
                ""
            }

            is UnknownHostException -> {
                "连接服务器失败，请检查网络"
            }

            is MalformedJsonException -> {
                "解析数据失败"
            }
            else -> ex.localizedMessage ?: return false
        }
        Log.i("msg", msg)
        return true
    }
}