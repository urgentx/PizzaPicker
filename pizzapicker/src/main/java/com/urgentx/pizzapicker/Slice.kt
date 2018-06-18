package com.urgentx.pizzapicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

@SuppressLint("ViewConstructor")
class Slice : View {

    lateinit var oval: RectF
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val random = Random()

    private val startAngle: Float
    private val sweepAngle: Float

    init {
        paint.setShadowLayer(12F, 0F, 0F, Color.BLACK)
    }

    constructor(context: Context, startAngle: Float, sweepAngle: Float) : super(context) {
        this.startAngle = startAngle
        this.sweepAngle = sweepAngle
    }

    constructor(context: Context, attrs: AttributeSet, startAngle: Float, sweepAngle: Float) : super(context, attrs) {
        this.startAngle = startAngle
        this.sweepAngle = sweepAngle
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, startAngle: Float, sweepAngle: Float) : super(context, attrs, defStyleAttr) {
        this.startAngle = startAngle
        this.sweepAngle = sweepAngle
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            paint.setShadowLayer(12F, 3F, 3F, Color.RED)
            logcat("Slice touched.")
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
        canvas?.drawArc(oval, startAngle, sweepAngle, true, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        oval = RectF(0F, 0F, w.toFloat(), h.toFloat())
    }
}