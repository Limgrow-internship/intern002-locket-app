package com.intern002.locketapp.data.model

import androidx.annotation.DrawableRes

data class WidgetFrame(
    val id: String,
    val isLocked: Boolean,
    var isSelected: Boolean,
    @DrawableRes val backgroundRes: Int,
    val displayText: String? = null
)
