package com.urgentx.pizzapicker

import android.graphics.Color
import android.util.Log


fun logcat(message: String?) {
    Log.d("PizzaPicker", message)
}

private fun interpolate(a: Float, b: Float, proportion: Float): Float {
    return a + (b - a) * proportion
}

/** Returns an interpoloated color, between `a` and `b`  */
internal fun interpolateColor(a: Int, b: Int, proportion: Float): Int {
    val hsva = FloatArray(3)
    val hsvb = FloatArray(3)
    Color.colorToHSV(a, hsva)
    Color.colorToHSV(b, hsvb)
    for (i in 0..2) {
        hsvb[i] = interpolate(hsva[i], hsvb[i], proportion)
    }
    return Color.HSVToColor(hsvb)
}