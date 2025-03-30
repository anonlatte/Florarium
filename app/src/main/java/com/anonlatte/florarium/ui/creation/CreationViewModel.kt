package com.anonlatte.florarium.ui.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.data.domain.CareHolder
import com.anonlatte.florarium.data.domain.CareTask
import com.anonlatte.florarium.data.domain.CareType
import com.anonlatte.florarium.data.domain.Plant
import com.anonlatte.florarium.data.domain.PlantCreationData
import com.anonlatte.florarium.data.domain.PlantWithSchedule
import com.anonlatte.florarium.data.domain.RegularSchedule
import com.anonlatte.florarium.data.repository.IMainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreationViewModel @Inject constructor(
    private val mainRepository: IMainRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreationUiState())
    val uiState: StateFlow<CreationUiState> = _uiState.asStateFlow()

    private val _uiCommand = MutableSharedFlow<PlantCreationCommand>()
    val uiCommand = _uiCommand.asSharedFlow()

    /** Used to determine if new plant was created */
    private var wasPlantCreated: Boolean = false

    /** Used to determine if plant already exists in database or new one should be created */
    private var isPlantExist = false

    /** Used to determine if image files should be deleted */
    val keepCreatedImageFiles get() = !isPlantExist && wasPlantCreated

    fun addPlantToGarden() {
        val creationData = uiState.value.creationData
        val validation = validatePlantName(creationData.plant.name)
        _uiState.update { it.copy(validationState = validation) }
        if (validation is PlantCreationState.Idle) {
            wasPlantCreated = true
            addPlantToGarden(creationData.plant, creationData.careTasks)
        }
    }

    private fun addPlantToGarden(plant: Plant, careTasks: List<CareTask>) {
        TODO("Not yet implemented")
    }

    fun addPlant() {
        addPlantToGarden()
    }

    private fun addPlantToGarden(plant: Plant, schedule: RegularSchedule, careHolder: CareHolder) {
        viewModelScope.launch {
            if (!isPlantExist) {
                kotlin.runCatching {
                    mainRepository.createPlant(
                        plant = plant.copy(createdAt = Date().time),
                        regularSchedule = schedule,
                        careHolder = careHolder
                    )
                }.onSuccess {
                    _uiCommand.emit(PlantCreationCommand.PlantCreated)
                }.onFailure {
                    _uiState.update { it.copy(validationState = PlantCreationError.CouldNotCreatePlant) }
                }
            } else {
                updatePlant(plant, listOf())
            }
        }
    }

    private suspend fun updatePlant(plant: Plant, careTasks: List<CareTask>) {
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                mainRepository.updatePlant(plant)
                updateSchedule(careTasks)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        validationState = PlantCreationState.Success(
                            PlantCreationData(
                                plant,
                                careTasks
                            )
                        )
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(validationState = PlantCreationError.CouldNotCreatePlant) }
            }
        }
    }

    private fun updateSchedule(careTasks: List<CareTask>) {
        TODO("Not yet implemented")
    }

    private suspend fun updateSchedule(schedule: RegularSchedule) {
        mainRepository.updateSchedule(schedule)
    }

    fun updateSchedule(
        scheduleItemType: CareType?,
        defaultIntervalValue: Int = 0,
        lastCareValue: Int = 0,
    ) {
    }

    fun clearScheduleField(toCareType: CareType?) {
        updateSchedule(toCareType)
    }

    fun updatePlantImage(path: String) {
        _uiState.update { state ->
            val updatedPlant = state.creationData.plant.copy(imageUri = path)
            state.copy(creationData = state.creationData.copy(plant = updatedPlant))
        }
    }

    fun restoreData(plantToSchedule: PlantWithSchedule?) {
        isPlantExist = plantToSchedule?.plant != null
        if (!isPlantExist) return

        if (uiState.value.creationData.isNotEdited) {
            _uiState.update { state ->
                state.copy(
                    creationData = state.creationData.copy(
                        plant = plantToSchedule?.plant ?: state.creationData.plant,
                    )
                )
            }
            return
        }
        _uiState.update {
            it.copy(validationState = PlantCreationState.PlantRecreation(uiState.value.creationData))
        }
    }

    fun setPlantName(text: CharSequence?) {
        _uiState.update { state ->
            val updatedPlant = state.creationData.plant.copy(name = text.toString())
            state.copy(creationData = state.creationData.copy(plant = updatedPlant))
        }
        _uiState.update {
            it.copy(validationState = validatePlantName(text))
        }
    }

    fun onPlantNameChange(name: String) {
        _uiState.update { state ->
            val updatedPlant = state.creationData.plant.copy(name = name)
            state.copy(creationData = state.creationData.copy(plant = updatedPlant))
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
    }

    fun revokeData() {
        _uiState.update {
            it.copy(validationState = PlantCreationState.PlantRecreation(uiState.value.creationData))
        }
    }

    fun onImagePicked(uri: String?) {
        _uiState.update { state ->
            val updatedPlant = state.creationData.plant.copy(imageUri = uri.orEmpty())
            state.copy(creationData = state.creationData.copy(plant = updatedPlant))
        }
    }

    fun onTakePhoto(uri: String) {
        _uiState.update { state ->
            val updatedPlant = state.creationData.plant.copy(imageUri = uri)
            state.copy(creationData = state.creationData.copy(plant = updatedPlant))
        }
    }

    fun onRequestCameraPermission(permission: String) {
        // Здесь можно добавить обработку разрешения, если она потребуется в будущем
    }

    fun addTask() {
        // Пока что не используется: задачи извлекаются из расписания
    }

    fun removeTask(index: Int) {
        // Пока что не используется: задачи извлекаются из расписания
    }

    private val PlantCreationData.isNotEdited: Boolean
        get() = plant == Plant()

    companion object {
        private const val MAX_PLANT_NAME_LENGTH = 40
    }
}
