package com.anonlatte.florarium.ui.creation

sealed interface PlantCreationError : PlantCreationState {
    object NameIsEmpty : PlantCreationError
    object NameIsTooLong : PlantCreationError

    object CouldNotCreatePlant : PlantCreationError
}