package com.anonlatte.florarium.ui.creation

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anonlatte.florarium.data.domain.ScheduleType
import kotlinx.parcelize.Parcelize

/**
 * Data class for [com.anonlatte.florarium.ui.custom.CareScheduleItem] view
 * @param title title of the item
 * @param icon icon of the item
 * @param intervalValue value of the slider
 * @param scheduleItemType type of the schedule item
 */
@Parcelize
data class CareScheduleItemData(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val scheduleItemType: ScheduleType,
    val intervalValue: Int = 0,
    val lastCareValue: Int = 0,
) : Parcelable