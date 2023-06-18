package com.anonlatte.florarium.ui.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.app.utils.getTimestampFromDaysAgo
import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.data.model.RegularSchedule
import com.anonlatte.florarium.data.model.ScheduleType
import com.anonlatte.florarium.data.repository.IMainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

class CreationViewModel @Inject constructor(
    private val mainRepository: IMainRepository
) : ViewModel() {

    private val _plantCreationState = MutableStateFlow<PlantCreationState>(
        PlantCreationState.Default()
    )
    val plantCreationState = _plantCreationState.asStateFlow()

    /** Used to determine if new plant was created */
    private var wasPlantCreated: Boolean = false

    /** Used to determine if plant already exists in database or new one should be created */
    private var isPlantExist = false

    /** Used to determine if image files should be deleted */
    val keepCreatedImageFiles get() = !isPlantExist && wasPlantCreated

    fun addPlantToGarden() {
        wasPlantCreated = true
        viewModelScope.launch {
            plantCreationState.collect {
                when (it) {
                    is PlantCreationState.Creating -> addPlantToGarden(it.plant, it.schedule)
                    is PlantCreationState.Default -> addPlantToGarden(it.plant, it.schedule)
                    else -> Unit
                }
            }
        }
    }

    private suspend fun addPlantToGarden(plant: Plant, schedule: RegularSchedule) {
        if (!isPlantExist) {
            mainRepository.createPlant(
                plant = plant.copy(createdAt = Date().time),
                regularSchedule = schedule,
            )
            _plantCreationState.emit(PlantCreationState.Created)
        } else {
            updatePlant(plant, schedule)
        }
    }


    fun addPlantAlarm(plantAlarm: PlantAlarm) {
        viewModelScope.launch {
            mainRepository.createPlantAlarm(plantAlarm)
        }
    }

    private fun updatePlant(plant: Plant, schedule: RegularSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.updatePlant(plant)
            updateSchedule(schedule)
        }
    }

    private suspend fun updateSchedule(schedule: RegularSchedule) {
        mainRepository.updateSchedule(schedule)
    }

    fun updateSchedule(
        schedule: RegularSchedule,
        scheduleItemType: ScheduleType?,
        defaultIntervalValue: Int? = null,
        lastCareValue: Int? = null
    ) {
        val updatedSchedule = when (scheduleItemType) {
            ScheduleType.WATERING -> {
                schedule.copy(
                    wateringInterval = defaultIntervalValue,
                    wateredAt = getTimestampFromDaysAgo(lastCareValue)
                )
            }

            ScheduleType.SPRAYING -> {
                schedule.copy(
                    sprayingInterval = defaultIntervalValue,
                    sprayedAt = getTimestampFromDaysAgo(lastCareValue)
                )
            }

            ScheduleType.FERTILIZING -> {
                schedule.copy(
                    fertilizingInterval = defaultIntervalValue,
                    fertilizedAt = getTimestampFromDaysAgo(lastCareValue)
                )
            }

            ScheduleType.ROTATING -> {
                schedule.copy(
                    rotatingInterval = defaultIntervalValue,
                    rotatedAt = getTimestampFromDaysAgo(lastCareValue)
                )
            }

            null -> {
                Timber.e("Unknown schedule type")
                schedule
            }
        }
        _plantCreationState.update {
            if (it is PlantCreationState.Default) {
                it.copy(schedule = updatedSchedule)
            } else {
                it
            }
        }
    }

    fun clearScheduleField(schedule: RegularSchedule, toScheduleType: ScheduleType?) {
        updateSchedule(schedule, toScheduleType)
    }

    fun updatePlantImage(path: String) {
        _plantCreationState.update { state ->
            if (state is PlantCreationState.Default) {
                val updatedPlant = state.plant.copy(imageUri = path)
                state.copy(updatedPlant)
            } else {
                state
            }

        }
    }

    fun restorePlant(srcPlant: Plant) {
        isPlantExist = true
        _plantCreationState.update { state ->
            if (state is PlantCreationState.Default) {
                state.copy(srcPlant)
            } else {
                state
            }

        }
    }

    fun restoreSchedule(schedule: RegularSchedule) {
        _plantCreationState.update { state ->
            if (state is PlantCreationState.Default) {
                state.copy(schedule = schedule)
            } else {
                state
            }

        }
    }

    fun setPlantName(text: CharSequence?) {
        _plantCreationState.update { state ->
            if (state is PlantCreationState.Default) {
                val updatedPlant = state.plant.copy(name = text.toString())
                state.copy(updatedPlant)
            } else {
                state
            }

        }
    }
}
