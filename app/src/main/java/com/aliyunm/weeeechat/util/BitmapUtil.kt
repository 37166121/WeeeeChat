package com.aliyunm.weeeechat.util

import android.graphics.Bitmap
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.View

object BitmapUtil {

    /**
     * @param view          需要模糊的View
     * @param blurRadius    模糊程度
     * @return              模糊处理后的Bitmap
     */
    fun blur(view : View, blurRadius: Float): View {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.REPEAT))
        }
        return view
    }

    /**
     * 给Bitmap添加脉冲噪声
     */
    fun impulseNoise(source: Bitmap): Bitmap {
        return source
    }
}