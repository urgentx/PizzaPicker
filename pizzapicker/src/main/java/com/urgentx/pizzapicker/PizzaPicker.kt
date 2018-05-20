package com.urgentx.pizzapicker

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.slice.view.*

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


    private lateinit var mContext: Context
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

        iconDrawable = arr.getDrawable(R.styleable.PizzaPicker_ppIconDrawable)
        color = arr.getColor(R.styleable.PizzaPicker_ppColor, mContext.resources.getColor(R.color.default_color))

        findViewById<ImageView>(R.id.icon).setImageDrawable(iconDrawable)
        setBackgroundColor(color)

        arr.recycle()
    }


}
