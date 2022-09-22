package com.aliyunm.weeeechat.data.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.aliyunm.weeeechat.data.model.RoomModel

@Dao
interface RoomDao {
    @Query("SELECT * FROM room")
    fun getAllRoom() : List<RoomModel>

    @Query("SELECT * FROM room WHERE rid = :rid")
    fun getRoom(rid : Int) : RoomModel?

    @Insert
    fun insertAll(vararg room: RoomModel)

    @Query("DELETE FROM room")
    fun deleteAll()

    @Delete
    fun delete(room: RoomModel)
}