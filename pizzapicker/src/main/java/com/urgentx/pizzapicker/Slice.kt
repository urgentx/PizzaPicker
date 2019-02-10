package com.urgentx.pizzapicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.urgentx.pizzapicker.models.SliceModel
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
    private val icon = ImageView(context)
    private val text = TextView(context)

    init {
        paint.setShadowLayer(12F, 0F, 0F, Color.BLACK)
        setWillNotDraw(false) //enable drawing in this ViewGroup
        title.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        title.textSize = 25F
        addView(title)
        text.layoutParams = RelativeLayout.LayoutParams(600, RelativeLayout.LayoutParams.WRAP_CONTENT)
        text.alpha = 0F //Initially not seen
        addView(text)
        icon.layoutParams = RelativeLayout.LayoutParams(200, 200)
        icon.alpha = 0F
    }

    //TODO: Investigate alternatives for telescoping constructors.
    constructor(context: Context, startAngle: Float, sweepAngle: Float, model: SliceModel) : super(context) {
        this.startAngle = startAngle
        this.originalStartAngle = startAngle
        this.sweepAngle = sweepAngle
        this.originalSweepAngle = sweepAngle
        title.text = model.getTitle()
        text.text = model.getText()
        model.getIconRes()?.let {
            icon.setImageResource(it)
            addView(icon)
        }
        paint.color = ContextCompat.getColor(context, model.getColorRes())
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
        title.x = centerAdjustingForDimension(title.measuredWidth, w, xOffset)
        title.y = centerAdjustingForDimension(title.measuredHeight, h, yOffset)
        text.x = centerAdjustingForDimension(text.measuredWidth, w, xOffset)
        text.y = centerAdjustingForDimension(text.measuredHeight, h, yOffset)
        text.layoutParams.width = w / 2
        icon.x = centerAdjustingForDimension(icon.measuredWidth, w, xOffset)
        icon.y = centerAdjustingForDimension(icon.measuredHeight, h, yOffset)
    }

    /**Calculates the X position for an item to appear in the center of the Layout**/
    private fun centerAdjustingForDimension(itemDimension: Int, layoutDimension: Int, offset: Float) = layoutDimension / 2 - itemDimension / 2F + offset * 10 //Can use either width or height as the widget is a square

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

    private fun animateArc() {
        //Interpolate over arc angles to achieve animation effect
        bringToFront()
        currentAnimDisposable?.dispose() //End previous animation
        currentAnimDisposable = Observable.interval(4, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    animateSlice()
                }.addTo(compositeDisposable)
    }

    private fun animateSlice() {
        val angleToCover = 360 - originalSweepAngle
        if (open) animationProgress += 1 else animationProgress -= 1
        if (animationProgress in 0F..240F) {
            val progress = animationProgress / 240F
            val animationElapsed = interpolator.getInterpolation(if (open) progress else 1 - progress)
            //Set position of Slice bounds
            val (leftOffset, topOffset, rightOffset, bottomOffset) = getOffsetsForOval(open, animationElapsed)
            currentOval.left = if (open) normalOval.left + leftOffset else fullOval.left + leftOffset
            currentOval.top = if (open) normalOval.top + topOffset else fullOval.top + topOffset
            currentOval.right = if (open) normalOval.right + rightOffset else fullOval.right + rightOffset
            currentOval.bottom = if (open) normalOval.bottom + bottomOffset else fullOval.bottom + bottomOffset
            //Calculate position of flying in TextView
            val textOffset = if (open) 24 * animationElapsed else 24 * (1 - animationElapsed)
            text.x = width / 2 - text.measuredWidth / 2F - (xOffset * textOffset) + xOffset * 10 + xOffset * 10
            text.y = height / 2 - text.measuredHeight / 2F - (yOffset * textOffset) + yOffset * 10 + yOffset * 10
            text.alpha = if (open) animationElapsed else 1 - animationElapsed
            icon.alpha = if (open) animationElapsed else 1 - animationElapsed //TODO: animate position of icon and title
            //Calculate arc angle for opening/closing progress
            if (open) {
                sweepAngle = originalSweepAngle + (animationElapsed * angleToCover)
                startAngle = originalStartAngle - (animationElapsed * angleToCover / 2)
            } else {
                sweepAngle = originalSweepAngle + angleToCover - (animationElapsed * angleToCover)
                startAngle = originalStartAngle - angleToCover / 2 + (animationElapsed * angleToCover / 2)
            }
        } else {
            currentOval = RectF(if (open) fullOval else normalOval) //Final positions
            currentAnimDisposable?.dispose() //Animation done; we're finished with this Disposable
        }
        invalidate()
    }

    private data class Offsets(val left: Float, val top: Float, val right: Float, val bottom: Float)

    /**
     * Calculates how much to add to or subtract from the source oval (either full or normal) for this current point in the animation.
     */
    private fun getOffsetsForOval(open: Boolean, animationElapsed: Float): Offsets {
        val xDiff = (fullOval.centerX() - normalOval.centerX())
        val yDiff = (fullOval.centerY() - normalOval.centerY())
        val leftOffset = (if (open) xDiff - margin else -(xDiff - margin)) * animationElapsed
        val topOffset = (if (open) yDiff - margin else -(yDiff - margin)) * animationElapsed
        val rightOffset = (if (open) xDiff + margin else -(xDiff + margin)) * animationElapsed
        val bottomOffset = (if (open) yDiff + margin else -(yDiff + margin)) * animationElapsed
        return Offsets(leftOffset, topOffset, rightOffset, bottomOffset)
    }

    override fun onDraw(canvas: Canvas?) {
        //Draw an arc from the start of the angle along $sweepAngle distance of perimeter
        canvas?.drawArc(currentOval, startAngle, sweepAngle, true, paint)
        super.onDraw(canvas)
    }
}