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
import android.view.ViewGroup
import android.view.animation.*
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("ViewConstructor")
class Slice : RelativeLayout {

    private lateinit var normalOval: RectF //Normal bounds
    private lateinit var fullOval: RectF //Expanded bounds
    private lateinit var currentOval: RectF
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val model: SliceModel
    private var startAngle: Float
    private val originalStartAngle: Float
    private var sweepAngle: Float
    private val originalSweepAngle: Float
    private var margin = 30

    private val compositeDisposable = CompositeDisposable()
    private var currentAnimDisposable: Disposable? = null

    private val interpolator = AccelerateDecelerateInterpolator()
    private var animationProgress = 0F
    private var open = false

    private val textView = TextView(context)

    init {
        paint.setShadowLayer(12F, 0F, 0F, Color.BLACK)
        setWillNotDraw(false) //enable drawing in this ViewGroup
        textView.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        addView(textView)
    }

    //TODO: Investigate alternatives for telescoping constructors.
    constructor(context: Context, startAngle: Float, sweepAngle: Float, model: SliceModel) : super(context) {
        this.startAngle = startAngle
        this.originalStartAngle = startAngle
        this.sweepAngle = sweepAngle
        this.originalSweepAngle = sweepAngle
        this.model = model
        textView.text = model.text
        paint.color = ContextCompat.getColor(context, model.colorRes)
    }

    constructor(context: Context, attrs: AttributeSet, startAngle: Float, sweepAngle: Float, model: SliceModel) : this(context, startAngle, sweepAngle, model)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, startAngle: Float, sweepAngle: Float, model: SliceModel) : this(context, startAngle, sweepAngle, model)

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
        currentOval = RectF(normalOval)
        textView.x = w / 2 - textView.measuredWidth / 2F + xOffset * 10
        textView.y = h / 2 - textView.measuredHeight / 2F + yOffset * 10
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val xDistanceFromCenter = event.x - width / 2
            val yDistanceFromCenter = event.y - height / 2

            val distance = sqrt(xDistanceFromCenter * xDistanceFromCenter + yDistanceFromCenter * yDistanceFromCenter)

            var angle = Math.toDegrees(Math.atan2(yDistanceFromCenter.toDouble(), xDistanceFromCenter.toDouble()))
            if (angle < 0) angle += 360

            return if (angle in startAngle..startAngle + sweepAngle && distance < width / 2) {
                val midAngle = (startAngle + sweepAngle) / 2
                logcat("angle: (${cos(midAngle) * (width / 2)}, ${sin(midAngle) * (width / 2)})")
                open = !open
                animateArc()
                true //Consume touch
            } else {
                false
            }
        }
        return false
    }

    private val growthRate = 29F

    private fun animateArc() {
        //Interpolate over arc angles to achieve animation effect
        logcat("normal: $normalOval, current: $currentOval")
        bringToFront()
        currentAnimDisposable?.dispose() //End previous animation
        currentAnimDisposable = Observable.interval(4, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (open) animationProgress += 1 else animationProgress -= 1
                    if (animationProgress in 0F..240F) {
                        val progress = animationProgress / 240F
                        val animationElapsed = interpolator.getInterpolation(if (open) progress else 1 - progress)
                        if (open) {
                            sweepAngle = originalSweepAngle + (animationElapsed * 270)
                            startAngle = originalStartAngle - (animationElapsed * 135)
                            val xDiff = (fullOval.centerX() - normalOval.centerX())
                            val yDiff = (fullOval.centerY() - normalOval.centerY())
                            currentOval.top = normalOval.top + (yDiff - growthRate) * animationElapsed
                            currentOval.bottom = normalOval.bottom + (yDiff + growthRate) * animationElapsed
                            currentOval.left = normalOval.left + (xDiff - growthRate) * animationElapsed
                            currentOval.right = normalOval.right + (xDiff + growthRate) * animationElapsed
                        } else {
                            sweepAngle = originalSweepAngle + 270 - (animationElapsed * 270)
                            startAngle = originalStartAngle - 135 + (animationElapsed * 135)
                            val xDiff = (fullOval.centerX() - normalOval.centerX())
                            val yDiff = (fullOval.centerY() - normalOval.centerY())
                            currentOval.top = fullOval.top - (yDiff - growthRate) * animationElapsed
                            currentOval.bottom = fullOval.bottom - (yDiff + growthRate) * animationElapsed
                            currentOval.left = fullOval.left - (xDiff - growthRate) * animationElapsed
                            currentOval.right = fullOval.right - (xDiff + growthRate) * animationElapsed
                        }
                    } else {
                        currentOval = RectF(if (open) fullOval else normalOval)
                        currentAnimDisposable?.dispose()
                    }
                    invalidate()
                }.addTo(compositeDisposable)
    }

    override fun onDraw(canvas: Canvas?) {
        //Draw an arc from the start of the angle along $sweepAngle distance of perimeter
        canvas?.drawArc(currentOval, startAngle, sweepAngle, true, paint)
        super.onDraw(canvas)
    }

    data class SliceModel(val title: String, val text: String?, val iconRes: Int?, val colorRes: Int)
}