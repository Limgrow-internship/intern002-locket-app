package com.intern002.locketapp.utils

import com.intern002.locketapp.data.remote.model.auth.GoogleRegistrationInfo

sealed class Result<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Result<T>(data)
    class Error<T>(message: String, data: T? = null) : Result<T>(data, message)
    class Loading<T>(data: T? = null) : Result<T>(data)
    class RegistrationRequired<T>(val registrationInfo: GoogleRegistrationInfo) : Result<T>()
}
