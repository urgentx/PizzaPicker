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
import android.view.animation.BounceInterpolator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("ViewConstructor")
class Slice : View {

    lateinit var normalOval: RectF //Normal bounds
    lateinit var fullOval: RectF //Expanded bounds
    lateinit var currentOval: RectF
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val random = Random()

    private var startAngle: Float
    private var sweepAngle: Float
    private val originalSweepAngle: Float
    private var margin = 30

    private val compositeDisposable = CompositeDisposable()

    init {
        paint.setShadowLayer(12F, 0F, 0F, Color.BLACK)
        paint.color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    constructor(context: Context, startAngle: Float, sweepAngle: Float) : super(context) {
        this.startAngle = startAngle
        this.sweepAngle = sweepAngle
        this.originalSweepAngle = sweepAngle
    }

    constructor(context: Context, attrs: AttributeSet, startAngle: Float, sweepAngle: Float) : this(context, startAngle, sweepAngle)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, startAngle: Float, sweepAngle: Float) : this(context, startAngle, sweepAngle)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val xDistance = event.x - width / 2
            val yDistance = event.y - height / 2

            val distance = sqrt(xDistance * xDistance + yDistance * yDistance)

            var angle = Math.toDegrees(Math.atan2(yDistance.toDouble(), xDistance.toDouble()))
            if (angle < 0) angle += 360

            return if (angle > startAngle && angle < startAngle + sweepAngle && distance < width / 2) {
                val midAngle = (startAngle + sweepAngle) / 2
                animateArc(currentOval == normalOval)
                logcat("angle: (${cos(midAngle) * (width / 2)}, ${sin(midAngle) * (width / 2)})")
                true
            } else {
                false
            }
        }
        return false
    }

    private fun animateArc(open: Boolean) {
        val interpolator = BounceInterpolator()
        currentOval = if (open) fullOval else normalOval
        Observable.interval(4, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val durationElapsed = (it / 240F)
                    if (durationElapsed < 1) {
                        val animationElapsed = interpolator.getInterpolation(durationElapsed)
                        sweepAngle = if (open){
                            originalSweepAngle + (animationElapsed * 180)
                        } else {
                            originalSweepAngle + 180 - (animationElapsed * 180)
                        }
                    }
                    invalidate()
                }.addTo(compositeDisposable)
    }

    override fun onDraw(canvas: Canvas?) {
        //Draw an arc from the start of the angle along $sweepAngle distance of perimeter
        canvas?.drawArc(currentOval, startAngle, sweepAngle, true, paint)
        val midAngle = startAngle + (sweepAngle / 2)
        paint.textSize = 70F
        canvas?.drawText("$midAngle", startAngle * 2, 80F, paint)
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //Determine size, position of Slice
        fullOval = RectF(x, y, w.toFloat(), h.toFloat())
        normalOval = RectF(x + margin, y + margin, w.toFloat() - margin, h.toFloat() - margin)
        //Need to convert angle to radians and offset the position based on the mid angle
        val midAngle = Math.toRadians(((startAngle + (sweepAngle / 2))).toDouble())
        val xOffset = margin * (cos(midAngle)).toFloat()
        val yOffset = margin * (sin(midAngle)).toFloat()
        normalOval.offset(xOffset, yOffset)
        currentOval = normalOval
    }
}