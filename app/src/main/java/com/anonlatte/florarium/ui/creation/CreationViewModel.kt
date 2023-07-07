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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

data class PlantCreationData(
    val plant: Plant = Plant(),
    val schedule: RegularSchedule = RegularSchedule(),
)

class CreationViewModel @Inject constructor(
    private val mainRepository: IMainRepository,
) : ViewModel() {

    private val _plantCreationState = MutableStateFlow<PlantCreationState>(PlantCreationState.Idle)
    val plantCreationState = _plantCreationState.asStateFlow()

    private val _plantCreationData = MutableStateFlow(PlantCreationData())
    val plantCreationData = _plantCreationData.asStateFlow()

    private val _uiCommand = MutableSharedFlow<PlantCreationCommand>()
    val uiCommand = _uiCommand.asSharedFlow()

    /** Used to determine if new plant was created */
    private var wasPlantCreated: Boolean = false

    /** Used to determine if plant already exists in database or new one should be created */
    private var isPlantExist = false

    /** Used to determine if image files should be deleted */
    val keepCreatedImageFiles get() = !isPlantExist && wasPlantCreated

    fun addPlantToGarden() {
        val creationData = _plantCreationData.value
        _plantCreationState.value = validatePlantName(creationData.plant.name)
        if (_plantCreationState.value is PlantCreationState.Idle) {
            wasPlantCreated = true
            addPlantToGarden(creationData.plant, creationData.schedule)
        }
    }

    private fun addPlantToGarden(plant: Plant, schedule: RegularSchedule) {
        viewModelScope.launch {
            if (!isPlantExist) {
                mainRepository.createPlant(
                    plant = plant.copy(createdAt = Date().time),
                    regularSchedule = schedule,
                )
                _uiCommand.emit(PlantCreationCommand.PlantCreated)
            } else {
                updatePlant(plant, schedule)
            }
        }
    }


    fun addPlantAlarm(plantAlarm: PlantAlarm) {
        viewModelScope.launch {
            mainRepository.createPlantAlarm(plantAlarm)
        }
    }

    private suspend fun updatePlant(plant: Plant, schedule: RegularSchedule) {
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                mainRepository.updatePlant(plant)
                updateSchedule(schedule)
            }.onSuccess {
                _plantCreationState.emit(
                    PlantCreationState.Success(
                        PlantCreationData(
                            plant,
                            schedule
                        )
                    )
                )
            }.onFailure {
                _plantCreationState.emit(PlantCreationError.CouldNotCreatePlant)
            }
        }
    }

    private suspend fun updateSchedule(schedule: RegularSchedule) {
        mainRepository.updateSchedule(schedule)
    }

    fun updateSchedule(
        scheduleItemType: ScheduleType?,
        defaultIntervalValue: Int? = null,
        lastCareValue: Int? = null,
    ) {
        _plantCreationData.update {
            val schedule = it.schedule
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
            it.copy(schedule = updatedSchedule)
        }
    }

    fun clearScheduleField(toScheduleType: ScheduleType?) {
        updateSchedule(toScheduleType)
    }

    fun updatePlantImage(path: String) {
        _plantCreationData.update { state ->
            val updatedPlant = state.plant.copy(imageUri = path)
            state.copy(plant = updatedPlant)
        }
    }

    fun restorePlant(srcPlant: Plant) {
        isPlantExist = true
        _plantCreationData.update { state ->
            state.copy(plant = srcPlant)
        }
    }

    fun restoreSchedule(schedule: RegularSchedule) {
        _plantCreationData.update { state ->
            state.copy(schedule = schedule)
        }
    }

    fun setPlantName(text: CharSequence?) {
        _plantCreationData.update { state ->
            val updatedPlant = state.plant.copy(name = text.toString())
            state.copy(plant = updatedPlant)
        }
        _plantCreationState.update {
            validatePlantName(text)
        }
    }

    private fun validatePlantName(text: CharSequence?): PlantCreationState = when {
        text.isNullOrEmpty() -> {
            PlantCreationError.NameIsEmpty
        }

        text.length > MAX_PLANT_NAME_LENGTH -> {
            PlantCreationError.NameIsTooLong
        }

        else -> {
            PlantCreationState.Idle
        }
    }

    fun onScheduleItemClickListener(careScheduleItemData: CareScheduleItemData) {
        viewModelScope.launch {
            _uiCommand.emit(
                PlantCreationCommand.OpenScheduleScreen(
                    schedule = plantCreationData.value.schedule,
                    scheduleItemType = careScheduleItemData.scheduleItemType,
                    title = careScheduleItemData.title,
                    icon = careScheduleItemData.icon,
                )
            )
        }
    }

    companion object {
        private const val MAX_PLANT_NAME_LENGTH = 40
    }
}
