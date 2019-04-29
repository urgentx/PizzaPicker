package com.urgentx.pizzapickersample

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.urgentx.pizzapicker.models.SliceModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = "Buying an apartment, unit or townhouse usually means being part of a body corporate who handles the management and upkeep of the building and sometimes the entire vicinity."
        val dummies = listOf(
                DummyModel("1", R.color.colorAccent, text, null, R.drawable.ic_android_green_24dp, R.color.colorAccent, R.color.colorPrimary),
                DummyModel("2", R.color.colorAccent, text, null, R.drawable.ic_android_green_24dp, R.color.colorAccent, R.color.colorPrimary),
                DummyModel("3", R.color.colorAccent, text, null, R.drawable.ic_android_green_24dp, R.color.colorAccent, R.color.colorPrimary),
                DummyModel("4", R.color.colorAccent, text, null, R.drawable.ic_android_green_24dp, R.color.colorAccent, R.color.colorPrimary),
                DummyModel("5", R.color.colorAccent, text, null, R.drawable.ic_android_green_24dp, R.color.colorAccent, R.color.colorPrimary),
                DummyModel("6", R.color.colorAccent, text, null, R.drawable.ic_android_green_24dp, R.color.colorAccent, R.color.colorPrimary))
        pizza_picker.setItems(dummies)
    }

    data class DummyModel(val tit: String, val titCR: Int? = null, val txt: String?, val txtCR: Int?, val icRes: Int?, val closedColRes: Int, val openColRes: Int) : SliceModel {

        override fun getTitle(): String = tit

        override fun getTitleColorRes() = titCR

        override fun getText(): String? = txt

        override fun getTextColorRes() = txtCR

        override fun getIconRes(): Int? = icRes

        override fun getClosedBackgroundColorRes() = closedColRes

        override fun getOpenBackgroundColorRes() = openColRes
    }
}
