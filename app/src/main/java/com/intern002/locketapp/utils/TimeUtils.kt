package com.intern002.locketapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun getTimeAgo(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""

        try {
            var cleanDateString = dateString.replace("T", " ")

            if (cleanDateString.length > 19) {
                cleanDateString = cleanDateString.substring(0, 19)
            }
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val past = format.parse(cleanDateString) ?: return "now"
            val now = Date()

            val diff = now.time - past.time - TimeUnit.HOURS.toMillis(7)

            if (diff < 0 && diff > -300000) return "now"

            val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            val weeks = days / 7

            return when {
                seconds < 60 -> "now"
                minutes < 60 -> "${minutes}m"
                hours < 24 -> "${hours}h"
                days < 7 -> "${days}d"
                else -> "${weeks}w"
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return "now"
        }
    }
}