package com.aliyunm.weeeechat.data.model

import com.aliyunm.weeeechat.data.model.MessageModel.Companion.PRIVATE

data class TModel<T>(val t : T, val type : Int = PRIVATE) {

}