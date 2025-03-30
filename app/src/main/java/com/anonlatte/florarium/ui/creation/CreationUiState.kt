package com.anonlatte.florarium.ui.creation

import com.anonlatte.florarium.data.domain.CareTask
import com.anonlatte.florarium.data.domain.Plant
import com.anonlatte.florarium.data.domain.PlantCreationData
import java.util.UUID

data class CreationUiState(
    val creationData: PlantCreationData = PlantCreationData(
        plant = Plant(
            id = UUID.randomUUID().hashCode().toLong(),
            name = "",
            imageUri = "",
            createdAt = System.currentTimeMillis()
        ),
        careTasks = listOf(
            CareTask.Watering("Watering", 7),
        )
    ),
    val validationState: PlantCreationState = PlantCreationState.Idle
)