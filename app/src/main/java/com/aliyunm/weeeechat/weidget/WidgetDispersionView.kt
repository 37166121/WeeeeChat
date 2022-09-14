package com.aliyunm.weeeechat.weidget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.PathInterpolator
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import kotlin.properties.Delegates

class WidgetDispersionView : View {

    private var mContext: Context
    private var mAttrs : AttributeSet?
    private var mDefStyleAttr : Int
    private var mDefStyleRes : Int

    /**
     * 图形大小
     */
    private val size : Float = 1500f
    private var mWidth : Int = 0
    // 添加一个观察者委托
    private var mHeight : Int by Delegates.observable(0) { property, oldValue, newValue ->
        // 观察到值改变后创建Bitmap
        createBitmap()
    }
    private lateinit var mCanvas : Canvas
    private lateinit var bitmap : Bitmap
    private var mMatrix : Matrix
    private var mPaint : Paint

    constructor(context : Context) : this(context, null)

    constructor(context : Context, attrs : AttributeSet?) : this(context, attrs, 0)

    constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int, defStyleRes : Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        mContext = context
        mAttrs = attrs
        mDefStyleAttr = defStyleAttr
        mDefStyleRes = defStyleRes
        mMatrix = Matrix()
        mPaint = Paint()
    }

    /**
     * 创建Bitmap
     */
    private fun createBitmap() {
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(bitmap)
    }

    /**
     * 控制是否重绘图形
     */
    private var redraw = true

    override fun onDraw(canvas: Canvas) {
        if (redraw) {
            drawShape(mCanvas)
            redraw = false
        }
        canvas.drawBitmap(bitmap, mMatrix, mPaint)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = getWidthSize(widthMeasureSpec, getMode(widthMeasureSpec))
        mHeight = getHeightSize(heightMeasureSpec, getMode(heightMeasureSpec))
        setMeasuredDimension(mWidth, mHeight)
    }

    private fun getMode(measureSpec : Int): Boolean {
        return MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY
    }

    private fun getWidthSize(measureSpec : Int, mode : Boolean): Int {
        val size : Int = if (mode) {
            MeasureSpec.getSize(measureSpec)
        } else {
            MeasureSpec.getSize(measureSpec) + paddingStart + paddingEnd
        }
        return size
    }

    private fun getHeightSize(measureSpec : Int, mode : Boolean): Int {
        val size : Int = if (mode) {
            MeasureSpec.getSize(measureSpec)
        } else {
            MeasureSpec.getSize(measureSpec) + paddingTop + paddingBottom
        }
        return size
    }

    /**
     * 随机颜色
     */
    private fun color() : Int {
        val r : Float = (Math.random() * 255).toFloat()
        val g : Float = (Math.random() * 255).toFloat()
        val b : Float = (Math.random() * 255).toFloat()
        val c = Color.valueOf(r, g, b)
        return c.toArgb()
    }

    /**
     * 添加透明度
     */
    private fun color(@ColorInt color : Int, alpha : Float = 0.3f) : Int {
        return ColorUtils.setAlphaComponent(color, (alpha * 255).toInt())
    }

    /**
     * 渐变
     */
    private fun linearGradient(left : Float = 0f, top : Float = 0f): LinearGradient {
        val position = floatArrayOf(0f, 1f)
        val baseColor = color()
        val color = intArrayOf(baseColor, color(baseColor, 0.5f))
        return LinearGradient(left, top, left + size, top + size, color, position, Shader.TileMode.REPEAT)
    }

    /**
     * 形状和大小
     */
    private fun drawShape(canvas: Canvas, paint : Paint = Paint()) {
        val mLeft = randomWidth()
        val mTop = randomHeight()
        paint.apply {
            color = color()
            shader = linearGradient(mLeft, mTop)
        }
        val rect = RectF(mLeft, mTop, left + size, top + size)
        shape(canvas, rect, paint.apply {
            isDither = true
        })
        fadeIn(this)
        bessel()
    }

    private fun randomWidth(): Float {
        return (Math.random() * width - size / 2).toFloat()
    }

    private fun randomHeight(): Float {
        return (Math.random() * height - size / 2).toFloat()
    }

    /**
     * 随机形状
     */
    private fun shape(canvas: Canvas, rect : RectF, paint : Paint = Paint()) {
        val random = (Math.random() * 3).toInt()
        when(random) {
            // 让出现圆形的几率更大
            0, 1 -> {
                // 圆形
                canvas.drawOval(rect, paint)
            }
            2 -> {
                // 矩形
                canvas.drawRect(rect, paint)
            }
        }
    }

    /**
     * 淡入
     */
    private fun fadeIn(view : View) {
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            duration = 5000
            addUpdateListener {
                invalidate()
            }
            start()
        }
    }

    /**
     * 淡出
     */
    private fun fadeOut(view : View) {
        ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
            duration = 5000
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    // 淡出结束后重绘图形
                    redraw = true
                    invalidate()
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }
            })
            start()
        }
    }

    /**
     * 随机贝塞尔曲线运动轨迹
     */
    private fun bessel() {
        // 路线（运动多少次）
        val size = 10
        // 时长(会随着插值器修改)
        val length = 8000L

        val path = Path().apply {
            for (i in 0 until size) {
                quadTo(randomWidth(), randomHeight(), randomWidth(), randomHeight())
            }
        }
        val pathInterpolator = PathInterpolator(0.2f, 0f, 0.4f, 1f)
        val traslateAnimator = ObjectAnimator.ofFloat(this, "x", "y", path).apply {
            addUpdateListener {
                postInvalidate()
            }
        }
        AnimatorSet().apply {
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    fadeOut(this@WidgetDispersionView)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            playTogether(traslateAnimator)
            interpolator = pathInterpolator
            duration = size * length
            start()
        }
    }
}