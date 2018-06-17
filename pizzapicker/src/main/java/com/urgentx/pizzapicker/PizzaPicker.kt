package com.urgentx.pizzapicker

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.slice.view.*

class PizzaPicker : LinearLayout {

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
    private var view: View? = null

    //Attributes
    private var iconDrawable: Drawable? = null
        set(it) {
            icon.setImageDrawable(it)
        }
    private var color: Int = 0
        set(it) {
            setBackgroundColor(it)
        }

    private fun initView() {
        this.view = this
        View.inflate(mContext, R.layout.slice, this)

        val arr = mContext.obtainStyledAttributes(attributes, R.styleable.PizzaPicker, styleAttr
                ?: 0, 0)

        iconDrawable = arr.getDrawable(R.styleable.PizzaPicker_pp_icon_drawable)
        color = arr.getColor(R.styleable.PizzaPicker_pp_color, ContextCompat.getColor(mContext, R.color.default_color))

        val numSlices = arr.getInteger(R.styleable.PizzaPicker_pp_num_slices, 0)
        createSlices(numSlices)

        arr.recycle()
    }

    private fun createSlices(numSlices: Int) {
        for (i in 0..numSlices) {
            val slice = Slice(context)
            addViewInLayout(slice, i, ViewGroup.LayoutParams(200, 200))
        }
    }
}
