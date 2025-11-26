package com.intern002.locketapp.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val email: String,
    val username: String? = null,
    val createdAt: String,
    val updatedAt: String
)
