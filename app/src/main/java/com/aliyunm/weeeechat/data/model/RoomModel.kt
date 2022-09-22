package com.aliyunm.weeeechat.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity(tableName = "room")
data class RoomModel(
    /**
     * 主键
     */
    @PrimaryKey var id : String = UUID.randomUUID().toString(),
    /**
     * 房间id
     */
    @ColumnInfo var rid : Int = 0,
    /**
     * 房间名称
     */
    @ColumnInfo var name : String = "",
    /**
     * 房间在线人数
     */
    @ColumnInfo var count : Int = 0,
    /**
     * 房间类型
     */
    @ColumnInfo var type : Int = ROOM
) : Serializable {

    /**
     * 聊天记录
     */
    @Ignore var messages : ArrayList<ChatModel> = arrayListOf()

    companion object {
        /**
         * 房间
         */
        const val ROOM = 0x0001

        /**
         * 私聊房间
         */
        const val PRIVATE = 0x0002
    }
}