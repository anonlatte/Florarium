package com.anonlatte.florarium.ui.creation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anonlatte.florarium.R
import com.anonlatte.florarium.data.model.ScheduleType

class CareScheduleItemData(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    @StringRes val scheduleValue: Int = R.string.value_not_set,
    val scheduleItemType: ScheduleType,
)