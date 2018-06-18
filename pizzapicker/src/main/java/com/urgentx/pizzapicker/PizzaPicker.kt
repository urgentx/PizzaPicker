package com.urgentx.pizzapicker

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout

class PizzaPicker : RelativeLayout {

    constructor(context: Context) : super(context) {
        mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        this.attributes = attrs
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        this.attributes = attrs
        this.styleAttr = defStyleAttr
        initView()
    }

    private val mContext: Context
    private var attributes: AttributeSet? = null
    private var styleAttr: Int? = null

    private var color: Int = 0
        set(it) {
            setBackgroundColor(it)
        }

    private var numSlices = 5

    private fun initView() {
        LayoutInflater.from(mContext).inflate(R.layout.slice, this, true)
        View.inflate(mContext, R.layout.slice, this)

        val arr = mContext.obtainStyledAttributes(attributes, R.styleable.PizzaPicker, styleAttr
                ?: 0, 0)

        color = arr.getColor(R.styleable.PizzaPicker_pp_color, ContextCompat.getColor(mContext, R.color.default_color))

        numSlices = arr.getInteger(R.styleable.PizzaPicker_pp_num_slices, 0)
        createSlices(numSlices)

        arr.recycle()
    }

    private fun createSlices(numSlices: Int) {
        for (i in 0..numSlices) {
            val slice = Slice(context, i * (360F / numSlices) + 5, 360F / numSlices - 5)
            addViewInLayout(slice, i, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }

    }
}
