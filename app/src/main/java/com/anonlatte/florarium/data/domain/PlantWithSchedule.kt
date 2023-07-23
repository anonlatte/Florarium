package com.anonlatte.florarium.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PlantWithSchedule(
    val plant: Plant,
    val schedule: RegularSchedule,
    val careHolder: CareHolder,
) : Parcelable