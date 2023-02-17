package com.anonlatte.florarium.ui.home

import com.anonlatte.florarium.data.model.PlantWithSchedule

sealed interface HomeScreenState {
    data class Default(val plantsToSchedules: List<PlantWithSchedule>) : HomeScreenState
    object Loading : HomeScreenState
}