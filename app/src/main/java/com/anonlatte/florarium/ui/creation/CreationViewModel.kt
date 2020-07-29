package com.anonlatte.florarium.ui.creation

import androidx.lifecycle.ViewModel
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.db.models.WinterSchedule
import java.util.Date

class CreationViewModel : ViewModel() {
    var plant: Plant? = null
    var regularSchedule: RegularSchedule = RegularSchedule()
    var winterSchedule: WinterSchedule = WinterSchedule()

    fun getTimestampFromDaysAgo(days: Int): Long =
        Date(System.currentTimeMillis() - (days * DAY_IN_MILLIS)).time

    fun getDaysFromTimestampAgo(timestamp: Long?): Int {
        return if (timestamp != null) {
            ((System.currentTimeMillis() - timestamp) / DAY_IN_MILLIS).toInt()
        } else {
            0
        }
    }

    companion object {
        const val DAY_IN_MILLIS = (1000 * 60 * 60 * 24).toLong()
    }
}
