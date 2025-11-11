package com.intern002.locketapp.data.remote.model

data class CountryResponse(
    val cca2: String,
    val languages: Map<String, String>?
)