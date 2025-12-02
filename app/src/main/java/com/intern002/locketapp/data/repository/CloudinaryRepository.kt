package com.intern002.locketapp.data.repository

import android.net.Uri

interface CloudinaryRepository {
    suspend fun uploadMedia(uri: Uri, isVideo: Boolean): String
}