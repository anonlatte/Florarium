package com.anonlatte.florarium.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data object Home : Screen

    @Serializable
    data class AddPlant(val plantId: Long? = null) : Screen

    @Serializable
    data object PlantsList : Screen

    @Serializable
    data object Profile : Screen
}