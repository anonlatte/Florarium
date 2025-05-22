package com.anonlatte.florarium.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
sealed class CareTask(val id: String) : Parcelable {
    abstract val name: String
    abstract val intervalDays: Int

    @Serializable
    data class Watering(
        override val name: String,
        override val intervalDays: Int
    ) : CareTask("Watering")

    @Serializable
    data class Spraying(
        override val name: String,
        override val intervalDays: Int
    ) : CareTask("Spraying")

    @Serializable
    data class Fertilizing(
        override val name: String,
        override val intervalDays: Int
    ) : CareTask("Fertilizing")

    @Serializable
    data class Rotating(
        override val name: String,
        override val intervalDays: Int
    ) : CareTask("Rotating")
}