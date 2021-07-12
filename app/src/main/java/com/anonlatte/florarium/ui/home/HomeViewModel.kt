package com.anonlatte.florarium.ui.home

import androidx.lifecycle.ViewModel
import com.anonlatte.florarium.data.model.PlantWithSchedule
import com.anonlatte.florarium.data.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val plantsListFlow = flow { emit(mainRepository.getPlants()) }
    private val regularSchedulesListFlow = flow { emit(mainRepository.getRegularScheduleList()) }

    val plantsToSchedules: Flow<List<PlantWithSchedule>> = combine(
        plantsListFlow, regularSchedulesListFlow
    ) { plantsList, schedulesList ->
        plantsList.map { plant ->
            val associatedSchedule = schedulesList.firstOrNull { it.plantId == plant.plantId }
            PlantWithSchedule(plant, associatedSchedule)
        }
    }

    fun deletePlants(plants: List<PlantWithSchedule>): Flow<Int> = flow {
        if (plants.isEmpty()) {
            emit(0)
        } else {
            val deletedAmount = mainRepository.deletePlants(plants.map { it.plant })
            emit(deletedAmount)
        }
    }
}
