package com.anonlatte.florarium.ui.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.app.utils.getTimestampFromDaysAgo
import com.anonlatte.florarium.data.model.*
import com.anonlatte.florarium.data.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreationViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    var plant: Plant = Plant()
        private set
    var regularSchedule: RegularSchedule = RegularSchedule()
        private set
    var winterSchedule: WinterSchedule = WinterSchedule()
        private set
    private var isPlantExist = false

    private var isPlantCreatedData = MutableStateFlow(false)
    var isPlantCreated = isPlantCreatedData.asStateFlow()

    fun addPlantToGarden() {
        if (!isPlantExist) {
            viewModelScope.launch {
                mainRepository.createPlant(plant).also { plantId ->
                    regularSchedule = regularSchedule.copy(plantId = plantId)
                    winterSchedule = winterSchedule.copy(plantId = plantId)
                }
                addSchedule()
            }
        } else {
            updatePlant()
        }
    }

    fun addPlantAlarm(plantAlarm: PlantAlarm) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.createPlantAlarm(plantAlarm)
    }

    private suspend fun addSchedule() {
        mainRepository.addSchedule(regularSchedule, winterSchedule)
    }

    private fun updatePlant() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.updatePlant(plant)
            updateSchedule()
        }
    }

    private suspend fun updateSchedule() {
        mainRepository.updateSchedule(regularSchedule, winterSchedule)
    }

    fun updateSchedule(
        scheduleItemType: ScheduleType?,
        defaultIntervalValue: Int? = null,
        winterIntervalValue: Int? = null,
        lastCareValue: Int? = null
    ) {
        when (scheduleItemType) {
            ScheduleType.WATERING -> {
                regularSchedule = regularSchedule.copy(
                    wateringInterval = defaultIntervalValue,
                    wateredAt = getTimestampFromDaysAgo(lastCareValue)
                )
                winterSchedule = winterSchedule.copy(wateringInterval = winterIntervalValue)
            }
            ScheduleType.SPRAYING -> {
                regularSchedule = regularSchedule.copy(
                    sprayingInterval = defaultIntervalValue,
                    sprayedAt = getTimestampFromDaysAgo(lastCareValue)
                )
                winterSchedule = winterSchedule.copy(sprayingInterval = winterIntervalValue)
            }
            ScheduleType.FERTILIZING -> {
                regularSchedule = regularSchedule.copy(
                    fertilizingInterval = defaultIntervalValue,
                    fertilizedAt = getTimestampFromDaysAgo(lastCareValue)
                )
                winterSchedule = winterSchedule.copy(fertilizingInterval = winterIntervalValue)
            }
            ScheduleType.ROTATING -> {
                regularSchedule = regularSchedule.copy(
                    rotatingInterval = defaultIntervalValue,
                    rotatedAt = getTimestampFromDaysAgo(lastCareValue)
                )
                winterSchedule = winterSchedule.copy(rotatingInterval = winterIntervalValue)
            }
        }
    }

    fun clearScheduleField(toScheduleType: ScheduleType?) = updateSchedule(toScheduleType)
    fun updatePlantImage(path: String?) {
        plant.imageUrl = path
    }

    fun setPlant(srcPlant: Plant) {
        plant = srcPlant
    }

    fun setSchedule(schedule: RegularSchedule) {
        regularSchedule = schedule
    }

    fun updatePlantExistence(exists: Boolean) {
        isPlantExist = exists
    }

    fun updateIsPlantCreated(isCreated: Boolean) {
        isPlantCreatedData.value = isCreated
    }
}
