package com.intern002.locketapp.data.model

import androidx.annotation.DrawableRes

data class CalendarDay(
    val dayOfMonth: Int,
    @DrawableRes val imageRes: Int? = null,
    val isPlaceholder: Boolean = false,
    val showPlusIcon: Boolean = false
)
