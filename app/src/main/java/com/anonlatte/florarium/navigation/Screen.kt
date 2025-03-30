package com.anonlatte.florarium.navigation

import com.anonlatte.florarium.data.domain.PlantCreationData
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data object Home : Screen

    @Serializable
    data class AddPlant(val plantData: PlantCreationData?) : Screen
}