package com.intern002.locketapp.data.remote.api

import retrofit2.http.GET
import com.intern002.locketapp.data.remote.model.CountryResponse

interface LanguageApiService {
    @GET("all?fields=cca2,languages")
    suspend fun getCountries(): List<CountryResponse>
}