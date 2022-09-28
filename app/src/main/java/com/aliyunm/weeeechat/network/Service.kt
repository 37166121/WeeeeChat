package com.aliyunm.weeeechat.network

import com.aliyunm.weeeechat.network.ApiImpl.API

object Service {

    suspend fun getPublicKey(callback : (String) -> Unit) {
        val reader = API.getPublicKey().charStream()
        val publicKey = StringBuffer()
        reader.readLines().forEach {
            publicKey.append(it)
        }
        callback(publicKey.toString())
    }

    suspend fun getRoom(rid : Int, callback : (Int) -> Unit) {
        val reader = API.getRoom(rid).charStream()
        val count = StringBuffer()
        reader.readLines().forEach {
            count.append(it)
        }
        callback(count.toString().toInt())
    }
}