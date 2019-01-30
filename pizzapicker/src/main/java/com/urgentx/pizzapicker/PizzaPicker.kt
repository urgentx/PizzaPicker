package com.urgentx.pizzapicker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import com.urgentx.pizzapicker.models.SliceModel


class PizzaPicker : RelativeLayout {

    private val mContext: Context
    private var attributes: AttributeSet? = null
    private var styleAttr: Int? = null

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
        arr.recycle()
    }

    fun setItems(items: List<SliceModel>) {
        createSlices(items)
    }

    private fun createSlices(items: List<SliceModel>) {
        items.forEachIndexed { index, sliceModel ->
            val slice = Slice(context, index * (360F / (items.size)), 360F / (items.size), sliceModel)
            addView(slice, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
    }
}
