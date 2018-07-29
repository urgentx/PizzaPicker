package com.urgentx.pizzapicker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat


class PizzaPicker : RelativeLayout {

    private val mContext: Context
    private var attributes: AttributeSet? = null
    private var styleAttr: Int? = null

    private var color: Int = 0
        set(it) {
            setBackgroundColor(it)
        }

    var adapter: BaseAdapter? = null
        set(adapter) {
            adapter?.let { bindToAdapter(it) }
        }

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

    private fun initView() {
        LayoutInflater.from(mContext).inflate(R.layout.slice, this, true)
        View.inflate(mContext, R.layout.slice, this)

        val arr = mContext.obtainStyledAttributes(attributes, R.styleable.PizzaPicker, styleAttr
                ?: 0, 0)

        color = arr.getColor(R.styleable.PizzaPicker_pp_color, ContextCompat.getColor(mContext, R.color.default_color))
        arr.recycle()
    }

    private fun bindToAdapter(adapter: BaseAdapter) {
        val numSlices = adapter.count
        createSlices(numSlices)
    }

    private fun createSlices(numSlices: Int) {
        for (i in 0 until numSlices) {
            val slice = Slice(context, i * (360F / (numSlices)), 360F / (numSlices))
            addViewInLayout(slice, i, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
    }
}
