package com.anonlatte.florarium.app.utils

import java.util.Date

private const val DAY_IN_MILLIS = (1000 * 60 * 60 * 24).toLong()
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
