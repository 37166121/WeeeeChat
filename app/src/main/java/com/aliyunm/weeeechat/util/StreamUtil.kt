package com.aliyunm.weeeechat.util

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object StreamUtil {

    fun reader(inputStream : InputStream, name : String = "UTF-8"): StringBuffer {
        return reader(BufferedReader(InputStreamReader(inputStream, name)))
    }

    fun reader(sr : BufferedReader): StringBuffer {
        val str = StringBuffer()
        var line: String
        while (sr.readLine().also { line = it } != null) {
            str.append(line)
        }
        return str
    }
}