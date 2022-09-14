package com.aliyunm.weeeechat.data.model

import java.io.Serializable
import java.util.Date

data class ChatModel(

    val content : String = "",

    /**
     * 发送方
     */
    val user: UserModel = UserModel(),

    /**
     * 接收方 私聊 用户id
     */
    val toUid: String = "",

    /**
     * 接收方 群聊 房间id
     */
    val toRid: Int = 0,

    /**
     * 消息发布时间
     */
    val time: Long = Date().time
) : Serializable {
}