package com.anonlatte.florarium.app.utils

import android.text.format.DateUtils.DAY_IN_MILLIS
import java.util.Date

object TimeStampHelper {
    val Int.daysAsMillis: Long
        get() = this * DAY_IN_MILLIS

    fun getTimestampFromDaysAgo(days: Int?): Long? {
        return if (days == null) {
            null
        } else {
            Date(System.currentTimeMillis() - (days * DAY_IN_MILLIS)).time
        }
    }

    fun getDaysFromTimestampAgo(timestamp: Long?): Int {
        return if (timestamp != null) {
            ((System.currentTimeMillis() - timestamp) / DAY_IN_MILLIS).toInt()
        } else {
            0
        }
    }
}