package com.urgentx.pizzapicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.Button

class Slice: Button {

    val oval = RectF(0F, 0F, 200F, 200F)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG);
    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = (ContextCompat.getColor(context, R.color.default_color))
        canvas?.drawArc(oval, 0f, 100f, true, paint)
    }
}