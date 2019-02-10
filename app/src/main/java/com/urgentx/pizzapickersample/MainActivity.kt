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
                DummyModel("Title", text, R.drawable.ic_android_green_24dp, R.color.material_blue_grey_800),
                DummyModel("Title", text, R.drawable.abc_ic_arrow_drop_right_black_24dp, R.color.material_blue_grey_800),
                DummyModel("Title", text, R.drawable.abc_ic_arrow_drop_right_black_24dp, R.color.material_blue_grey_800),
                DummyModel("Title", text, null, R.color.material_blue_grey_800),
                DummyModel("Title", text, null, R.color.material_blue_grey_800),
                DummyModel("Title", text, null, R.color.material_blue_grey_800))
        pizza_picker.setItems(dummies)
    }

    data class DummyModel(val tit: String, val txt: String?, val icRes: Int?, val colRes: Int) : SliceModel {
        override fun getTitle(): String = tit

        override fun getText(): String? = txt

        override fun getIconRes(): Int? = icRes

        override fun getColorRes(): Int = colRes
    }
}
