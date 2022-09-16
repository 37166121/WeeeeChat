package com.aliyunm.weeeechat.data.model

import java.io.Serializable
import java.util.Date

data class ChatModel(

    val type : Int = MessageModel.SPECIFY,

    val content : String = "",

    /**
     * 发送方
     */
    val user: UserModel = UserModel(),

    /**
     * 接收方 私聊 用户id
     */
    val uid: String = "",

    /**
     * 接收方 群聊 房间id
     */
    val rid: Int = 0,

    /**
     * 消息发布时间
     */
    val time: Long = Date().time
) : Serializable {
}