package com.aliyunm.weeeechat.util

import android.content.Context
import android.content.res.Resources

object ScaleUtil {

    fun dp2Px(context: Context, dps: Int): Int {
        return dp2Px(context.resources, dps)
    }

    fun dp2Px(resources: Resources, dps: Int): Int {
        return Math.round(resources.displayMetrics.density * dps)
    }
}