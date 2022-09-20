package com.aliyunm.weeeechat.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    companion object {
        const val IP = "192.168.2.82"
        const val SOCKET_PORT = 9999

        const val PORT = 55550
        const val BASEURL = "http://192.168.2.82:$PORT"
        const val SOCKET = "/s/"
        const val GET_PUBLIC_KEY = "getPublicKey/"

        const val ROOM = "/room/"
        const val GET_ROOM = "getRoom/{rid}"
    }

    @GET("$SOCKET$GET_PUBLIC_KEY")
    suspend fun getPublicKey() : ResponseBody

    @GET("$ROOM$GET_ROOM")
    suspend fun getRoom(@Path("rid") rid : Int) : ResponseBody
}