package com.intern002.locketapp.data.repository

import com.intern002.locketapp.data.remote.api.FcmApi
import com.intern002.locketapp.data.remote.model.fcm.RegisterFcmTokenRequest
import javax.inject.Inject

class FcmRepository @Inject constructor(
    private val fcmApi: FcmApi
) {
    suspend fun registerToken(token: String) {
        try {
            val request = RegisterFcmTokenRequest(token)
            fcmApi.registerToken(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
