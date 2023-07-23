package com.anonlatte.florarium.ui.creation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anonlatte.florarium.data.domain.CareHolder
import com.anonlatte.florarium.data.domain.RegularSchedule
import com.anonlatte.florarium.data.domain.ScheduleType

sealed interface PlantCreationCommand {

    data class OpenScheduleScreen(
        val schedule: RegularSchedule,
        val careHolder: CareHolder,
        val scheduleItemType: ScheduleType,
        @StringRes val title: Int,
        @DrawableRes val icon: Int,
    ) : PlantCreationCommand

    object PlantCreated : PlantCreationCommand
}