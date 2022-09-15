package com.aliyunm.weeeechat

import android.app.Application
import android.util.Log
import android.widget.Toast
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
            Log.i(this::class.java.name, if (it) "连接成功" else "连接失败")
        }
    }
}