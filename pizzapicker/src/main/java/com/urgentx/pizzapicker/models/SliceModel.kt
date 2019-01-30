package com.urgentx.pizzapicker.models

interface SliceModel {
    /**Text to display on the Slice as a title.*/
    fun getTitle(): String

    /**Text to display on the Slice as a detail text.*/
    fun getText(): String?

    /**Resource ID for the Slice icon.*/
    fun getIconRes(): Int?

    /**Resource ID for the color of the Slice.*/
    fun getColorRes(): Int
}