package com.aliyunm.weeeechat.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.data.model.RoomModel
import com.aliyunm.weeeechat.data.repository.DatabaseHelper
import com.aliyunm.weeeechat.network.socket.SocketManage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {
    val search : MutableLiveData<String> = MutableLiveData()
    val rooms : ArrayList<RoomModel> = arrayListOf()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val s = DatabaseHelper.getAllRoom()
            s.forEach {
                enterRoom(ChatModel(type = MessageModel.ENTER_ROOM, rid = it.rid)) {

                }
            }
        }
    }

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
}