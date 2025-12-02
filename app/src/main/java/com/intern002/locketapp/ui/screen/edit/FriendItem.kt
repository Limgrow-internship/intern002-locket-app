package com.intern002.locketapp.ui.screen.edit

data class FriendItem(
    val id: String,
    val name: String,
    var isSelected: Boolean = false,
    val isAllButton: Boolean = false,
    val avatarUrl: String? = null
)