package com.anonlatte.florarium.ui.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.app.utils.TimeStampHelper.getTimestampFromDaysAgo
import com.anonlatte.florarium.data.domain.CareHolder
import com.anonlatte.florarium.data.domain.CareType
import com.anonlatte.florarium.data.domain.Plant
import com.anonlatte.florarium.data.domain.PlantWithSchedule
import com.anonlatte.florarium.data.domain.RegularSchedule
import com.anonlatte.florarium.data.repository.IMainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
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
            addPlantToGarden(creationData.plant, creationData.schedule, creationData.careHolder)
        }
    }

    private fun addPlantToGarden(plant: Plant, schedule: RegularSchedule, careHolder: CareHolder) {
        viewModelScope.launch {
            if (!isPlantExist) {
                mainRepository.createPlant(
                    plant = plant.copy(createdAt = Date().time),
                    regularSchedule = schedule,
                    careHolder = careHolder
                )
            } else {
                updatePlant(plant, schedule)
            }
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
        scheduleItemType: CareType?,
        defaultIntervalValue: Int = 0,
        lastCareValue: Int = 0,
    ) {
        _plantCreationData.update {
            val schedule = it.schedule
            val careHolder = it.careHolder
            val updatedSchedule = when (scheduleItemType) {
                CareType.WATERING -> {
                    schedule.copy(wateringInterval = defaultIntervalValue)
                }

                CareType.SPRAYING -> {
                    schedule.copy(sprayingInterval = defaultIntervalValue)
                }

                CareType.FERTILIZING -> {
                    schedule.copy(fertilizingInterval = defaultIntervalValue)
                }

                CareType.ROTATING -> {
                    schedule.copy(rotatingInterval = defaultIntervalValue)
                }

                null -> {
                    Timber.e("Unknown schedule type")
                    schedule
                }
            }
            val updatedCareHolder = when (scheduleItemType) {
                CareType.WATERING -> {
                    careHolder.copy(wateredAt = getTimestampFromDaysAgo(lastCareValue) ?: 0)
                }

                CareType.SPRAYING -> {
                    careHolder.copy(sprayedAt = getTimestampFromDaysAgo(lastCareValue) ?: 0)
                }

                CareType.FERTILIZING -> {
                    careHolder.copy(fertilizedAt = getTimestampFromDaysAgo(lastCareValue) ?: 0)
                }

                CareType.ROTATING -> {
                    careHolder.copy(rotatedAt = getTimestampFromDaysAgo(lastCareValue) ?: 0)
                }

                null -> {
                    Timber.e("Unknown schedule type")
                    careHolder
                }
            }
            it.copy(schedule = updatedSchedule, careHolder = updatedCareHolder)
        }
    }

    fun clearScheduleField(toCareType: CareType?) {
        updateSchedule(toCareType)
    }

    fun updatePlantImage(path: String) {
        _plantCreationData.update { state ->
            val updatedPlant = state.plant.copy(imageUri = path)
            state.copy(plant = updatedPlant)
        }
    }

    fun restoreData(plantToSchedule: PlantWithSchedule?) {
        isPlantExist = plantToSchedule?.plant != null
        if (!isPlantExist) return

        if (_plantCreationData.value.isNotEdited) {
            _plantCreationData.update { state ->
                state.copy(
                    plant = plantToSchedule?.plant ?: state.plant,
                    schedule = plantToSchedule?.schedule ?: state.schedule
                )
            }
            return
        }
        _plantCreationState.update {
            PlantCreationState.PlantRecreation(
                PlantCreationData(
                    _plantCreationData.value.plant,
                    _plantCreationData.value.schedule
                )
            )
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
                    careHolder = plantCreationData.value.careHolder,
                    scheduleItemType = careScheduleItemData.scheduleItemType,
                    title = careScheduleItemData.title,
                    icon = careScheduleItemData.icon,
                )
            )
        }
    }

    fun revokeData() {
        _plantCreationState.update {
            PlantCreationState.PlantRecreation(
                PlantCreationData(
                    _plantCreationData.value.plant,
                    _plantCreationData.value.schedule
                )
            )
        }
    }

    private val PlantCreationData.isNotEdited: Boolean
        get() = plant == Plant() && schedule == RegularSchedule()

    companion object {
        private const val MAX_PLANT_NAME_LENGTH = 40
    }
}
