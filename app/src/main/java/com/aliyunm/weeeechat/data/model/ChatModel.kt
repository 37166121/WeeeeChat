package com.aliyunm.weeeechat.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.aliyunm.weeeechat.util.SharedPreferencesUtil
import java.io.Serializable
import java.util.Date
import java.util.UUID

@Entity(tableName = "chat")
data class ChatModel(
    /**
     * 消息id
     */
    @PrimaryKey var id : String = UUID.randomUUID().toString(),
    /**
     * 消息类型
     */
    @ColumnInfo var type : Int = MessageModel.SPECIFY,
    /**
     * 消息内容
     */
    @ColumnInfo var content : String = "",
    /**
     * 发送方id
     */
    @ColumnInfo var fromUid : String = SharedPreferencesUtil.getString("uid", UUID.randomUUID().toString()),
    /**
     * 发送方昵称
     */
    @ColumnInfo var nickname : String = SharedPreferencesUtil.getString("nickname", ""),

    /**
     * 接收方 私聊 用户id
     */
    @ColumnInfo var uid: String = "",

    /**
     * 接收方 群聊 房间id
     */
    @ColumnInfo var rid: Int = 0,

    /**
     * 消息发布时间
     */
    @ColumnInfo var time: Long = Date().time
) : Serializable {
}