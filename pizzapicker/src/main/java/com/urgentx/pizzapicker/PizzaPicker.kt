package com.urgentx.pizzapicker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import com.urgentx.pizzapicker.models.SliceModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*


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
        val slices = items.mapIndexed { index, sliceModel ->
            val slice = Slice(context, index * (360F / (items.size)), 360F / (items.size), sliceModel)
            addView(slice, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
            slice
        }
        setupSliceInterpolation(slices)

    }

    private fun setupSliceInterpolation(slices: List<Slice>) {
        //TODO: keep track of previous slices selected, to interpolate their color back to normal
//        val obs = Stack<Observable<Float>>() //Stores latest exposed progress Observables from Slices
//        val sub = Stack<Disposable>() //Stores latest Disposables resulting from subscribing to a progress
//
//        slices.forEach { slice ->
//            slice.animProgress.subscribe {
//                if (sub.isNotEmpty()) sub.pop().dispose()
//                if (obs.isNotEmpty()) obs.pop()
//                sub.push(obs.peek().subscribe { slice.interpolateBackgroundColor(it) })
//                obs.push(slice.animProgress)
//            }
//        }

        val o = BehaviorSubject.create<Float>()

        slices.forEach {
            it.animProgress.subscribe(o)
        }

        o.subscribe { logcat(it.toString()) }



    }
}
