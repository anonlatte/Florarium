package com.anonlatte.florarium.ui.creation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anonlatte.florarium.data.domain.CareHolder
import com.anonlatte.florarium.data.domain.CareType
import com.anonlatte.florarium.data.domain.RegularSchedule

sealed interface PlantCreationCommand {

    data class OpenScheduleScreen(
        val schedule: RegularSchedule,
        val careHolder: CareHolder,
        val scheduleItemType: CareType,
        @StringRes val title: Int,
        @DrawableRes val icon: Int,
    ) : PlantCreationCommand

    object PlantCreated : PlantCreationCommand
}