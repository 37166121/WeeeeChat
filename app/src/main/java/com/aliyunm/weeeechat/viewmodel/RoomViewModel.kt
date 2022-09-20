package com.aliyunm.weeeechat.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.data.model.RoomModel
import com.aliyunm.weeeechat.network.Service
import com.aliyunm.weeeechat.network.socket.SocketManage
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {
    val search : MutableLiveData<String> = MutableLiveData()
    val rooms : ArrayList<RoomModel> = arrayListOf()

    fun onMessage(owner : LifecycleOwner, callback : (ChatModel) -> Unit) {
        SocketManage.chatMessage.observe(owner) {
            callback(it)
        }
    }

    fun enterRoom(chatModel: ChatModel, callback : (Boolean) -> Unit) {
        SocketManage.sendMessage(MessageModel(type = MessageModel.ENTER_ROOM, content = chatModel)) {
            callback(it)
        }
    }

    fun getRoom(rid : Int, callback : (Int) -> Unit) {
        viewModelScope.launch {
            Service.getRoom(rid) {
                callback(it)
            }
        }
    }
}