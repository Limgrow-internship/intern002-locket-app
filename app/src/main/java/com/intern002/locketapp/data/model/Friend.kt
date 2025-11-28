package com.intern002.locketapp.data.model

import com.intern002.locketapp.ui.viewmodel.friends.FriendshipStatus

data class Friend(
    val id: String,
    val username: String,
    val discriminator: Int,
    val avatarUrl: String?,
    var status: FriendshipStatus
)
