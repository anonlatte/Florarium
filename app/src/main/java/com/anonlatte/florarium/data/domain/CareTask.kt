package com.anonlatte.florarium.data.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class CareTask(val id: String) {
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