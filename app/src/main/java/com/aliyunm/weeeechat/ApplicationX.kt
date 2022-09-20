package com.aliyunm.weeeechat

import android.app.Application
import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.aliyunm.weeeechat.exception.GlobalExceptionManager
import com.aliyunm.weeeechat.network.socket.SocketManage
import com.aliyunm.weeeechat.util.RSAUtil
import com.aliyunm.weeeechat.util.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApplicationX : Application() {

    override fun onCreate() {
        super.onCreate()
        initData()
    }

    private fun initData() {
        SharedPreferencesUtil.setSharedPreferences(getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE))
        RSAUtil.generateKeyPair()
        SocketManage.connect {
            Log.i(this::class.java.name, if (it) {
                "连接服务器成功"
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@ApplicationX, "连接服务器失败", Toast.LENGTH_SHORT).show()
                }
                "连接服务器失败"
            })
        }
        exception()
    }

    /**
     * 全局异常捕获
     */
    fun exception() {
        GlobalExceptionManager
    }
}