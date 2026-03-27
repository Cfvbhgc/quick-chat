package com.quickchat.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateFormatter {

    /**
     * Formats a timestamp into a human-readable relative string.
     * "Just now" -> "5 min ago" -> "2:30 PM" -> "Yesterday" -> "Mon" -> "Mar 15"
     */
    fun formatRelative(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes min ago"
            }
            isToday(timestamp) -> {
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
            }
            isYesterday(timestamp) -> "Yesterday"
            diff < TimeUnit.DAYS.toMillis(7) -> {
                SimpleDateFormat("EEE", Locale.getDefault()).format(Date(timestamp))
            }
            else -> {
                SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
            }
        }
    }

    fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
    }

    fun formatLastSeen(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "last seen just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "last seen $minutes min ago"
            }
            isToday(timestamp) -> {
                val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
                "last seen today at $time"
            }
            isYesterday(timestamp) -> {
                val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
                "last seen yesterday at $time"
            }
            else -> {
                val formatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(timestamp))
                "last seen $formatted"
            }
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
