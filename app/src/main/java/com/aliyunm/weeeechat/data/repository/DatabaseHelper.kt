package com.aliyunm.weeeechat.data.repository

import android.content.Context
import androidx.room.Room
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.RoomModel

object DatabaseHelper {

    private lateinit var db : ChatDatabase

    /**
     * 获取数据库
     */
    fun getDatabase(context : Context) {
        if (!this::db.isInitialized) {
            db = Room.databaseBuilder(context, ChatDatabase::class.java, "weeeechat")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    fun getAllChat() : List<ChatModel> {
        return db.chatDao().getAllChat()
    }

    fun insertChat(vararg chat: ChatModel) {
        db.chatDao().insertAll(*chat)
    }

    fun deleteAllChat() {
        db.chatDao().deleteAll()
    }

    fun deleteChat(chat: ChatModel) {
        db.chatDao().delete(chat)
    }

    fun getAllRoom() : List<RoomModel> {
        return db.roomDao().getAllRoom()
    }

    fun getRoom(rid : Int) : RoomModel? {
        return db.roomDao().getRoom(rid)
    }

    fun insertRoom(vararg room: RoomModel) {
        db.roomDao().insertAll(*room)
    }

    fun deleteAllRoom() {
        db.roomDao().deleteAll()
    }

    fun deleteRoom(room: RoomModel) {
        db.roomDao().delete(room)
    }

    /**
     * 清空房间和聊天记录
     */
    fun deleteAll() {
        deleteAllChat()
        deleteAllRoom()
    }
}