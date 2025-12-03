package com.intern002.locketapp.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object MediaSaver {
    suspend fun saveMediaToGallery(context: Context, sourceUri: Uri, isVideo: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    val timestamp = System.currentTimeMillis()
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "Locket_$timestamp")

                    if (isVideo) {
                        put(MediaStore.MediaColumns.MIME_TYPE, "VID/mp4")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_MOVIES + "/LocketApp"
                        )
                    } else {
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES + "/LocketApp"
                        )
                    }
                }
                val collection = if (isVideo) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                val destUri = resolver.insert(collection, contentValues) ?: return@withContext

                val inputStream: InputStream? = resolver.openInputStream(sourceUri)
                val outputStream: OutputStream? = resolver.openOutputStream(destUri)

                if (inputStream != null && outputStream != null) {
                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Saved to gallery!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    throw Exception("Can't open routine file")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error saved file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}