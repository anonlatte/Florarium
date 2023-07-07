package com.anonlatte.florarium.ui.creation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anonlatte.florarium.data.model.RegularSchedule
import com.anonlatte.florarium.data.model.ScheduleType

sealed interface PlantCreationCommand {

    data class OpenScheduleScreen(
        val schedule: RegularSchedule,
        val scheduleItemType: ScheduleType,
        @StringRes val title: Int,
        @DrawableRes val icon: Int
    ) : PlantCreationCommand

    object PlantCreated : PlantCreationCommand
}