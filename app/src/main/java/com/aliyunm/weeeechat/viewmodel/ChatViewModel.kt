package com.aliyunm.weeeechat.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.network.socket.SocketManage

class ChatViewModel : ViewModel() {
    var rid : Int = 0
    lateinit var uid : String
    val chatEnter : MutableLiveData<String> = MutableLiveData()

    fun onMessage(owner : LifecycleOwner, callback : (ChatModel) -> Unit) {
        SocketManage.chatMessage.observe(owner) {
            callback(it)
        }
    }

    fun sendMessage(type : Int, chatModel : ChatModel, callback : (Boolean) -> Unit = {}) {
        SocketManage.sendMessage(MessageModel(type, chatModel)) {
            callback(it)
        }
    }
}