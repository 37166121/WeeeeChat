package com.aliyunm.weeeechat.network.socket

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.aliyunm.weeeechat.SingleLiveEvent
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.CONNECT
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.OFFLINE
import com.aliyunm.weeeechat.data.model.RoomModel
import com.aliyunm.weeeechat.data.model.UserModel
import com.aliyunm.weeeechat.network.Api
import com.aliyunm.weeeechat.util.GsonUtil
import com.aliyunm.weeeechat.util.MessageUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.reflect.Type
import java.net.Socket
import kotlin.properties.Delegates

object SocketManage {

    var socket : Socket? = null

    /**
     * 定义读取数据的输入流
     */
    lateinit var reader : BufferedReader

    /**
     * 定义写数据的输出流
     */
    lateinit var writer : PrintWriter

    /**
     * 服务器公钥
     */
    var publicKey : String = ""

    /**
     * 收到消息发出通知
     */
    var _onMessage : (Any) -> Unit = {}

    var message : ChatModel by Delegates.observable(ChatModel()) { property, oldValue, newValue ->
        rooms.forEach {
            if (newValue.uid.isNotBlank()) {

            } else {
                if (it.rid == newValue.rid) {
                    it.messages.add(newValue)
                }
            }
        }
        chats.addNotice(newValue)
    }

    val rooms : ArrayList<RoomModel> = arrayListOf(RoomModel(0, "大厅"))

    val chats : ArrayList<ChatModel> = arrayListOf()

    fun ArrayList<ChatModel>.addNotice(chat : ChatModel) {
        this.add(chat)
        CoroutineScope(Dispatchers.Main).launch {
            chatMessage.value = chat
        }
    }

    val room_chats : ArrayList<ChatModel> = arrayListOf()

    val chatMessage : SingleLiveEvent<ChatModel> = SingleLiveEvent()

    fun connect(callback : (Boolean) -> Unit) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.i(this::class.java.name, "连接服务器")
                // 连接服务器
                socket = Socket(Api.IP, Api.SOCKET_PORT)
                // 第一次访问服务器返回公钥
                writer = PrintWriter(OutputStreamWriter(socket?.getOutputStream(), "UTF-8"))
                reader = BufferedReader(InputStreamReader(socket?.getInputStream(), "UTF-8"))

                callback(true)

                var message: String
                // 读数据
                while (reader.readLine().also { message = it } != null) {
                    onMessage(message)
                }

                // 关闭输入输出流
                writer.close()
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            job.start()
            // 如果十秒后没回应，判定为连接失败
            delay(10 * 1000)
            if (socket == null) {
                callback(false)
                job.cancel()
            }
        }
    }

    private fun onMessage(line : String) {
        Log.i(this::class.java.name, "收到消息")
        val s: List<String> = line.split("--------------------------")
        val messageModel = GsonUtil.fromJson(MessageUtil.decode(s[0], s[1]), MessageModel::class.java)
        when(messageModel.type) {
            // 收到服务器返回登录消息
            CONNECT -> {
                _onMessage(true)
            }

            else -> {
                val chatModel : ChatModel = setChatModel(GsonUtil.toJson(messageModel.content ?: ""))
                message = chatModel
            }
        }
    }

    fun<T> sendMessage(message: MessageModel<T>, callback : (Boolean) -> Unit = {}) {
        Log.i(this::class.java.name, "发送消息")
        CoroutineScope(Dispatchers.IO).launch {
            if (socket?.isClosed == false && socket?.isConnected == true) {
                val message = MessageUtil.encrypt(Gson().toJson(message), publicKey)
                writer.write(message)
                writer.flush()
                callback(true)
            } else {
                // 连接已断开
                connect {
                    if (it) {
                        sendMessage(message)
                    }
                }
            }
        }
    }

    fun setChatModel(json: String): ChatModel {
        val type: Type = object : TypeToken<ChatModel>() {}.type
        return GsonUtil.fromJson(json, type)
    }

    fun setUserModel(json: String): UserModel {
        val type: Type = object : TypeToken<UserModel>() {}.type
        return GsonUtil.fromJson(json, type)
    }

    fun onClose(callback : (Boolean) -> Unit = {}) {
        println("断开连接")
        sendMessage(MessageModel(OFFLINE, true)) {
            socket?.close()
            socket = null
            callback(true)
        }
    }
}