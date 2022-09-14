package com.aliyunm.weeeechat.util

import com.google.gson.Gson
import com.google.gson.internal.Primitives
import java.io.StringReader
import java.lang.reflect.Type

/**
 * 万能的 GsonUtil(Object... o)
 *
 * 仅仅写着玩，不要用在项目中，这样写可能会被同事打死
 */
object GsonUtil {

    private val gson = Gson()

    fun toJson(src: Any): String {
        return gson.toJson(src, src.javaClass)
    }

    fun <T> fromJson(json: String, clazz: Class<T>): T {
        val o = gson.fromJson<Any>(json, clazz as Type)
        return Primitives.wrap(clazz).cast(o)
    }

    fun <T> fromJson(json: String, type: Type): T {
        return run {
            val reader = StringReader(json)
            gson.fromJson(reader, type)
        }
    }
}