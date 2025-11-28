package com.intern002.locketapp.data.repository

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryRepositoryImpl @Inject constructor() : CloudinaryRepository {

    private val TAG = "CloudinaryRepo"

    override suspend fun uploadMedia(uri: Uri, isVideo: Boolean): String =
        suspendCancellableCoroutine { continuation ->

            val folderName = "locket_uploads"
            val type = if (isVideo) "video" else "image"

            val requestId = MediaManager.get().upload(uri)
                .option("resource_type", type)
                .option("folder", folderName)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "Start upload: $requestId")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            Log.d(TAG, "Success Upload: $url")
                            continuation.resume(url)
                        } else {
                            continuation.resumeWithException(Exception("Cloudinary can't return URL"))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e(TAG, "Error upload: ${error.description}")
                        continuation.resumeWithException(Exception(error.description))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                    }
                })
                .dispatch()

            continuation.invokeOnCancellation {
                MediaManager.get().cancelRequest(requestId)
            }
        }
}