package com.aliyunm.weeeechat.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.network.socket.SocketManage

class ChatViewModel : ViewModel() {
    val chatEnter : MutableLiveData<String> = MutableLiveData()

    fun onMessage(owner : LifecycleOwner, callback : (ChatModel) -> Unit) {
        SocketManage.chatMessage.observe(owner) {
            callback(it as ChatModel)
        }
    }

    fun sendMessage(type : Int, message : String) {
        SocketManage.sendMessage(MessageModel(type, ChatModel(content = message)))
    }
}