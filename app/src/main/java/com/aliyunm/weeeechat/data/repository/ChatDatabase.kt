package com.aliyunm.weeeechat.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.RoomModel
import com.aliyunm.weeeechat.data.repository.dao.ChatDao
import com.aliyunm.weeeechat.data.repository.dao.RoomDao

@Database(entities = [RoomModel::class, ChatModel::class], version = 2, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun roomDao(): RoomDao

    abstract fun chatDao(): ChatDao
}