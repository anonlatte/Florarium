package com.anonlatte.florarium.ui.creation

sealed interface PlantCreationState {
    data class Success(val plantCreationData: PlantCreationData) : PlantCreationState

    object Loading : PlantCreationState

    object Idle : PlantCreationState
}

sealed interface PlantCreationError : PlantCreationState {
    object NameIsEmpty : PlantCreationError
    object NameIsTooLong : PlantCreationError

    object CouldNotCreatePlant : PlantCreationError
}
