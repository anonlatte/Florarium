package com.anonlatte.florarium.ui.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.app.utils.getTimestampFromDaysAgo
import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.data.model.RegularSchedule
import com.anonlatte.florarium.data.model.ScheduleType
import com.anonlatte.florarium.data.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CreationViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    var plant: Plant = Plant()
        private set
    var regularSchedule: RegularSchedule = RegularSchedule()
        private set
    private var isPlantExist = false

    private var isPlantCreatedData = MutableStateFlow(false)
    var isPlantCreated = isPlantCreatedData.asStateFlow()
    fun addPlantToGarden() {
        if (!isPlantExist) {
            viewModelScope.launch {
                mainRepository.createPlant(
                    plant = plant,
                    regularSchedule = regularSchedule,
                )
                updateIsPlantCreated(true)
            }
        } else {
            updatePlant()
        }
    }

    fun addPlantAlarm(plantAlarm: PlantAlarm) {
        viewModelScope.launch {
            mainRepository.createPlantAlarm(plantAlarm)
        }
    }

    private fun updatePlant() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.updatePlant(plant)
            updateSchedule()
        }
    }

    private suspend fun updateSchedule() {
        mainRepository.updateSchedule(regularSchedule)
    }

    fun updateSchedule(
        scheduleItemType: ScheduleType?,
        defaultIntervalValue: Int? = null,
        lastCareValue: Int? = null
    ) {
        when (scheduleItemType) {
            ScheduleType.WATERING -> {
                regularSchedule = regularSchedule.copy(
                    wateringInterval = defaultIntervalValue,
                    wateredAt = getTimestampFromDaysAgo(lastCareValue)
                )
            }
            ScheduleType.SPRAYING -> {
                regularSchedule = regularSchedule.copy(
                    sprayingInterval = defaultIntervalValue,
                    sprayedAt = getTimestampFromDaysAgo(lastCareValue)
                )
            }
            ScheduleType.FERTILIZING -> {
                regularSchedule = regularSchedule.copy(
                    fertilizingInterval = defaultIntervalValue,
                    fertilizedAt = getTimestampFromDaysAgo(lastCareValue)
                )
            }
            ScheduleType.ROTATING -> {
                regularSchedule = regularSchedule.copy(
                    rotatingInterval = defaultIntervalValue,
                    rotatedAt = getTimestampFromDaysAgo(lastCareValue)
                )
            }
            null -> Timber.e("Unknown schedule type")
        }
    }

    fun clearScheduleField(toScheduleType: ScheduleType?) = updateSchedule(toScheduleType)

    fun updatePlantImage(path: String) {
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

    fun setPlantName(text: CharSequence?) {
        plant = plant.copy(name = text.toString())
    }
}
