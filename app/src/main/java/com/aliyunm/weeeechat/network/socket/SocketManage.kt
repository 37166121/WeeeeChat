package com.aliyunm.weeeechat.network.socket

import com.aliyunm.weeeechat.SingleLiveEvent
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.BROADCAST
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.CONNECT
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.ENTER_ROOM
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.OFFLINE
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.PRIVATE
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.QUIT_ROOM
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.SPECIFY
import com.aliyunm.weeeechat.network.Api
import com.aliyunm.weeeechat.util.GsonUtil
import com.aliyunm.weeeechat.util.MessageUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        chats.add(newValue)
        chatMessage.postValue(newValue)
    }

    val chats : ArrayList<ChatModel> = arrayListOf()

    val chatMessage : SingleLiveEvent<ChatModel> = SingleLiveEvent()

    fun connect(callback : (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                println("连接服务器")
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
                callback(false)
            }
        }
    }

    private fun onMessage(line : String) {
        println("收到消息")
        val s: List<String> = line.split("--------------------------")
        val messageModel = GsonUtil.fromJson(MessageUtil.decode(s[0], s[1]), MessageModel::class.java)
        when(messageModel.type) {
            // 收到服务器返回登录消息
            CONNECT -> {
                _onMessage(true)
            }

            OFFLINE -> {

            }
            // 收到广播
            BROADCAST -> {
                val chatModel : ChatModel = setChatBean(GsonUtil.toJson(messageModel.content ?: ""))
                message = chatModel
            }
            // 收到房间消息
            SPECIFY -> {
                val chatModel : ChatModel = setChatBean(GsonUtil.toJson(messageModel.content ?: ""))
                message = chatModel
            }
            // 收到私聊
            PRIVATE -> {
                val chatModel : ChatModel = setChatBean(GsonUtil.toJson(messageModel.content ?: ""))
                message = chatModel
            }

            ENTER_ROOM -> {

            }

            QUIT_ROOM -> {

            }
        }
    }

    fun<T> sendMessage(message: MessageModel<T>, callback : (Boolean) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            if (socket?.isConnected == true && socket?.isClosed == false) {
                val message = MessageUtil.encrypt(Gson().toJson(message), publicKey)
                writer.write(message)
                callback(true)
            } else {
                // 连接已断开
                connect {
                    if (it) {
                        sendMessage(message)
                    }
                }
            }
            writer.flush()
        }
    }


    fun setChatBean(json: String): ChatModel {
        val type: Type = object : TypeToken<ChatModel>() {}.type
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