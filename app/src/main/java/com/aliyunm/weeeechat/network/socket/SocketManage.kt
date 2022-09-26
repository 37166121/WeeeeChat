package com.aliyunm.weeeechat.network.socket

import android.util.Log
import com.aliyunm.weeeechat.SingleLiveEvent
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.CONNECT
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.ENTER_ROOM
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.OFFLINE
import com.aliyunm.weeeechat.data.model.MessageModel.Companion.QUIT_ROOM
import com.aliyunm.weeeechat.data.model.RoomModel
import com.aliyunm.weeeechat.data.repository.DatabaseHelper
import com.aliyunm.weeeechat.network.Api
import com.aliyunm.weeeechat.network.Service.getIP
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
import java.util.ArrayList
import java.lang.reflect.Type
import java.net.Socket
import kotlin.properties.Delegates

object SocketManage {

    var socket: Socket? = null

    /**
     * 读取数据的输入流
     */
    private lateinit var reader: BufferedReader

    /**
     * 写数据的输出流
     */
    private lateinit var writer: PrintWriter

    /**
     * 服务器公钥
     */
    var publicKey: String = ""

    /**
     * 收到消息发出通知
     */
    var onConnect: (Boolean) -> Unit = {}

    /**
     * 房间列表
     */
    val roomManager: ArrayList<RoomModel> = arrayListOf(RoomModel(rid = 0, name = "广场"))

    /**
     * 收到消息后发布通知
     * true : 自己进入房间
     * false : 其他人进入房间或者自己退出房间
     */
    val roomMessage: SingleLiveEvent<Boolean> = SingleLiveEvent()

    /**
     * 全局聊天记录
     */
    val chatManager: ArrayList<ChatModel> = arrayListOf<ChatModel>().apply {
        DatabaseHelper.getAllChat().forEach {
            add(it)
        }
    }

    /**
     * 收到消息后发布通知
     */
    val chatMessage: SingleLiveEvent<ChatModel> = SingleLiveEvent()

    fun ArrayList<ChatModel>.addChat(chat: ChatModel) {
        add(chat)
        // 数据库中过滤掉进入退出房间消息
        if (chat.content.isNotBlank()) {
            DatabaseHelper.insertChat(chat)
        }
        // 将消息添加到roomManager中
        roomManager.forEach {
            if (chat.uid.isNotBlank()) {

            } else {
                if (it.rid == chat.rid) {
                    it.messages.add(chat)
                }
            }
        }
        chatMessage.postValue(chat)
    }

    fun ArrayList<RoomModel>.addRoom(room: RoomModel) {
        // 如果以前在这个房间有聊天记录 进入房间后恢复记录
        DatabaseHelper.getAllChat().forEach {
            if (room.rid == it.rid) {
                room.messages.add(it)
            }
        }
        add(room)
    }

    /**
     * 连接socket服务器
     */
    fun connect(callback: (Boolean) -> Unit) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.i(this::class.java.name, "连接服务器")
                // 连接服务器
                socket = Socket(getIP(), Api.SOCKET_PORT)
                // 第一次访问服务器返回公钥
                writer = PrintWriter(OutputStreamWriter(socket?.getOutputStream(), "UTF-8"))
                reader = BufferedReader(InputStreamReader(socket?.getInputStream(), "UTF-8"))

                callback(true)

                var message: String
                // 阻塞 读数据
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

    /**
     * 收到消息
     */
    private fun onMessage(message: String) {
        Log.i(this::class.java.name, "收到消息")
        val s: List<String> = message.split("--------------------------")
        val messageModel = GsonUtil.fromJson(MessageUtil.decode(s[0], s[1]), MessageModel::class.java)
        when (messageModel.type) {

            // 收到服务器返回登录消息
            CONNECT -> {
                onConnect(true)
            }

            // 收到进入房间消息
            ENTER_ROOM -> {
                val roomModel: RoomModel = setRoomModel(GsonUtil.toJson(messageModel.content ?: ""))
                enterRoom(roomModel)
            }

            // 收到退出房间消息
            QUIT_ROOM -> {
                val chatModel: ChatModel = setChatModel(GsonUtil.toJson(messageModel.content ?: ""))
                quitRoom(chatModel)
            }

            else -> {
                val chatModel: ChatModel = setChatModel(GsonUtil.toJson(messageModel.content ?: ""))
                chatManager.addChat(chatModel)
            }
        }
    }

    /**
     * 发送消息
     */
    fun <T> sendMessage(message: MessageModel<T>, callback: (Boolean) -> Unit = {}) {
        Log.i(this::class.java.name, "发送消息")
        CoroutineScope(Dispatchers.IO).launch {
            if (socket?.isClosed == false && socket?.isConnected == true) {
                val message = MessageUtil.encrypt(Gson().toJson(message), publicKey)
                writer.write(message)
                writer.flush()
                callback(true)
            } else {
                // 连接已断开 重连
                connect {
                    if (it) {
                        sendMessage(message)
                    }
                }
            }
        }
    }

    fun getRoom(rid : Int) : RoomModel {
        roomManager.forEach {
            if (it.rid == rid) {
                return it
            }
        }
        return RoomModel()
    }

    private fun enterRoom(roomModel: RoomModel) {
        if (roomModel.messages.isEmpty()) {
            // 自己进入房间
            if (DatabaseHelper.getRoom(roomModel.rid) == null) {
                DatabaseHelper.insertRoom(roomModel)
            }
            roomManager.addRoom(roomModel)
            roomMessage.postValue(true)
        } else {
            // 其他人进入房间
            roomManager.forEach {
                if (it.rid == roomModel.rid) {
                    // 房间人数+1
                    it.count++
                }
            }
            roomMessage.postValue(false)
            chatManager.addChat(roomModel.messages[0])
        }
    }

    private fun quitRoom(chatModel: ChatModel) {
        roomManager.forEach {
            if (it.rid == chatModel.rid) {
                it.count--
            }
        }
        roomMessage.postValue(false)
        chatManager.addChat(chatModel)
    }

    private fun setChatModel(json: String): ChatModel {
        val type: Type = object : TypeToken<ChatModel>() {}.type
        return GsonUtil.fromJson(json, type)
    }

    private fun setRoomModel(json: String): RoomModel {
        val type: Type = object : TypeToken<RoomModel>() {}.type
        return GsonUtil.fromJson(json, type)
    }

    /**
     * 关闭当前socket连接
     */
    fun onClose(callback: (Boolean) -> Unit = {}) {
        println("断开连接")
        sendMessage(MessageModel(OFFLINE, true)) {
            socket?.close()
            socket = null
            callback(true)
        }
    }

    /**
     * 清空房间和聊天记录
     */
    fun clear() {
        roomManager.clear()
        chatManager.clear()
        roomManager.add(RoomModel(rid = 0, name = "广场"))
    }
}