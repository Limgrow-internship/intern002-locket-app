package com.intern002.locketapp.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Hàm mở rộng giúp lắng nghe bàn phím
fun View.setOnKeyboardVisibilityListener(onVisibilityChanged: (Boolean) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
        onVisibilityChanged(isKeyboardVisible)
        insets
    }
}