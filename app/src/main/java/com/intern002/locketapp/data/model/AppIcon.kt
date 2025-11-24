package com.intern002.locketapp.data.model

import androidx.annotation.DrawableRes

data class AppIcon(
    val id: String,
    @DrawableRes val iconRes: Int,
    var isSelected: Boolean
)
