package com.intern002.locketapp.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun getTimeAgo(dateString: String): String {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")

            val past = format.parse(dateString) ?: return "now"
            val now = java.util.Date()

            val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
            val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
            val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time)

            return when {
                seconds < 60 -> "${seconds}s"
                minutes < 60 -> "${minutes}m"
                hours < 24 -> "${hours}h"
                else -> "${days}d"
            }
        } catch (e: Exception) {
            return "now"
        }
    }
}