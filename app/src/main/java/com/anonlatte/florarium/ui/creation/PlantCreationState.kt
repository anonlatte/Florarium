package com.anonlatte.florarium.ui.creation

sealed interface PlantCreationState {
    data class Success(val plantCreationData: PlantCreationData) : PlantCreationState

    data class PlantRecreation(val data: PlantCreationData) : PlantCreationState

    object Loading : PlantCreationState

    object Idle : PlantCreationState
}

