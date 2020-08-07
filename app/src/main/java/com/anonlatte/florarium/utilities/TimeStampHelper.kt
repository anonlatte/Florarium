package com.anonlatte.florarium.utilities

import java.util.Date

object TimeStampHelper {
    private const val DAY_IN_MILLIS = (1000 * 60 * 60 * 24).toLong()

    fun getTimestampFromDaysAgo(days: Int): Long =
        Date(System.currentTimeMillis() - (days * DAY_IN_MILLIS)).time

    fun getDaysFromTimestampAgo(timestamp: Long?): Int {
        return if (timestamp != null) {
            ((System.currentTimeMillis() - timestamp) / DAY_IN_MILLIS).toInt()
        } else {
            0
        }
    }
}
