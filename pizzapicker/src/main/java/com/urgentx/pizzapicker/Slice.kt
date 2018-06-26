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
import kotlin.math.sqrt

@SuppressLint("ViewConstructor")
class Slice : View {

    lateinit var oval: RectF
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val random = Random()

    private val startAngle: Float
    private val sweepAngle: Float
    private var margin = 5

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
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val xDistance = event.x - width / 2
            val yDistance = event.y - height / 2

            val distance = sqrt(xDistance * xDistance + yDistance * yDistance)

            var angle = Math.toDegrees(Math.atan2(yDistance.toDouble(), xDistance.toDouble()))
            if (angle < 0) angle += 360

            return if (angle > startAngle && angle < startAngle + sweepAngle && distance < width / 2) {
                logcat("Touched Slice with Start angle: $startAngle, Sweep angle: $sweepAngle")
                true
            } else {
                false
            }
        }
        return false
    }

    override fun onDraw(canvas: Canvas?) {
        paint.color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
        canvas?.drawArc(oval, startAngle, sweepAngle, true, paint)
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        oval = RectF(x, y, w.toFloat(), h.toFloat())
    }
}