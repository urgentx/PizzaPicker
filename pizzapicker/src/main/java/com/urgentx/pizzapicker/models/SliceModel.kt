package com.urgentx.pizzapicker.models

interface SliceModel {
    /**Text to display on the Slice as a title.*/
    fun getTitle(): String

    /**Color resource ID of title.*/
    fun getTitleColorRes(): Int?

    /**Text to display on the Slice as a detail text.*/
    fun getText(): String?

    /**Color resource ID of text.*/
    fun getTextColorRes(): Int?

    /**Resource ID for the Slice icon.*/
    fun getIconRes(): Int?

    /**Resource ID for the color of the Slice in closed state.*/
    fun getClosedBackgroundColorRes(): Int

    /**Resource ID for the color of the Slice in open state.*/
    fun getOpenBackgroundColorRes(): Int
}