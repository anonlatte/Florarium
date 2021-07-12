package com.anonlatte.florarium.ui.creation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.app.utils.getTimestampFromDaysAgo
import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.data.model.RegularSchedule
import com.anonlatte.florarium.data.model.ScheduleType
import com.anonlatte.florarium.data.model.WinterSchedule
import com.anonlatte.florarium.data.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreationViewModel(application: Application) : AndroidViewModel(application) {
    var plant: Plant = Plant()
        private set
    var regularSchedule: RegularSchedule = RegularSchedule()
        private set
    var winterSchedule: WinterSchedule = WinterSchedule()
        private set
    private val mainRepository = MainRepository(application)
    private var isPlantExist = false

    private var isPlantCreatedData = MutableLiveData(false)
    var isPlantCreated: LiveData<Boolean> = isPlantCreatedData
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

    private fun addSchedule() {
        mainRepository.addSchedule(regularSchedule, winterSchedule)
    }

    private fun updatePlant() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.updatePlant(plant)
            updateSchedule()
        }
    }

    private fun updateSchedule() {
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
