package com.aliyunm.weeeechat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.CONNECT
import com.aliyunm.weeeechat.data.model.UserModel
import com.aliyunm.weeeechat.network.Service
import com.aliyunm.weeeechat.network.socket.SocketManage
import com.aliyunm.weeeechat.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val nickname : MutableLiveData<String> = MutableLiveData()

    fun login(userModel: UserModel, callback : (Boolean) -> Unit) {
        SocketManage._onMessage = {
            if (it as Boolean) {
                // 保存用户信息 uid 昵称
                SharedPreferencesUtil.putString("uid", userModel.uid)
                    .putString("nickname", userModel.nickname)
                    .commit()
            }
            callback(it)
        }
        SocketManage.sendMessage(MessageModel(type = CONNECT, content = userModel))
    }

    fun getPublicKey(callback : () -> Unit) {
        viewModelScope.launch {
            Service.getPublicKey {
                SharedPreferencesUtil.putString("serverPublicKey", it)
                    .commit()
                SocketManage.publicKey = it
                callback()
            }
        }
    }
}