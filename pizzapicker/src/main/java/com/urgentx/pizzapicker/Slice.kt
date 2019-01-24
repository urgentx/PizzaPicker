package com.urgentx.pizzapicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
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

    private var startAngle: Float
    private val originalStartAngle: Float
    private var sweepAngle: Float
    private val originalSweepAngle: Float
    private var margin = 30
    private var xOffset = 0F
    private var yOffset = 0F

    private val compositeDisposable = CompositeDisposable()
    private var currentAnimDisposable: Disposable? = null

    private val interpolator = AccelerateDecelerateInterpolator()
    private var animationProgress = 0F
    private var open = false

    private val title = TextView(context)
    private val text = TextView(context)

    init {
        paint.setShadowLayer(12F, 0F, 0F, Color.BLACK)
        setWillNotDraw(false) //enable drawing in this ViewGroup
        title.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        title.textSize = 25F
        addView(title)
        text.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        text.alpha = 0F //Initially not seen
        addView(text)
    }

    //TODO: Investigate alternatives for telescoping constructors.
    constructor(context: Context, startAngle: Float, sweepAngle: Float, model: SliceModel) : super(context) {
        this.startAngle = startAngle
        this.originalStartAngle = startAngle
        this.sweepAngle = sweepAngle
        this.originalSweepAngle = sweepAngle
        title.text = model.title
        text.text = model.text
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
        xOffset = margin * (cos(midAngle)).toFloat()
        yOffset = margin * (sin(midAngle)).toFloat()
        normalOval.offset(xOffset, yOffset)
        currentOval = RectF(normalOval)
        title.x = w / 2 - title.measuredWidth / 2F + xOffset * 10
        title.y = h / 2 - title.measuredHeight / 2F + yOffset * 10
        text.x = w / 2 - text.measuredWidth / 2F + xOffset * 10
        text.y = h / 2 - text.measuredHeight / 2F + yOffset * 10
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
                        if (open) { //TODO: Refactor into methods.
                            sweepAngle = originalSweepAngle + (animationElapsed * 270) //TODO: Derive remaining angle till complete
                            startAngle = originalStartAngle - (animationElapsed * 135)
                            val xDiff = (fullOval.centerX() - normalOval.centerX())
                            val yDiff = (fullOval.centerY() - normalOval.centerY())
                            currentOval.top = normalOval.top + (yDiff - growthRate) * animationElapsed
                            currentOval.bottom = normalOval.bottom + (yDiff + growthRate) * animationElapsed
                            currentOval.left = normalOval.left + (xDiff - growthRate) * animationElapsed
                            currentOval.right = normalOval.right + (xDiff + growthRate) * animationElapsed
                            text.x = width / 2 - text.measuredWidth / 2F - (xOffset * 15 * animationElapsed) + xOffset * 10
                            +xOffset * 10
                            text.y = height / 2 - text.measuredHeight / 2F - (yOffset * 15 * animationElapsed) + yOffset * 10
                            +yOffset * 10
                            text.alpha = animationElapsed
                        } else {
                            sweepAngle = originalSweepAngle + 270 - (animationElapsed * 270)
                            startAngle = originalStartAngle - 135 + (animationElapsed * 135)
                            val xDiff = (fullOval.centerX() - normalOval.centerX())
                            val yDiff = (fullOval.centerY() - normalOval.centerY())
                            currentOval.top = fullOval.top - (yDiff - growthRate) * animationElapsed
                            currentOval.bottom = fullOval.bottom - (yDiff + growthRate) * animationElapsed
                            currentOval.left = fullOval.left - (xDiff - growthRate) * animationElapsed
                            currentOval.right = fullOval.right - (xDiff + growthRate) * animationElapsed
                            text.x = width / 2 - text.measuredWidth / 2F - (xOffset * 15 * (1 - animationElapsed)) + xOffset * 10
                            +xOffset * 10
                            text.y = height / 2 - text.measuredHeight / 2F - (yOffset * 15 * (1 - animationElapsed)) + yOffset * 10
                            +yOffset * 10
                            text.alpha = 1 - animationElapsed
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