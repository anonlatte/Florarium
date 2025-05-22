package com.anonlatte.florarium.ui.creation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.data.domain.CareTask
import com.anonlatte.florarium.data.domain.Plant
import com.anonlatte.florarium.data.repository.IMainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPlantViewModel @Inject constructor(
    private val mainRepository: IMainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddPlantUiState>(AddPlantUiState.Loading)
    val uiState: StateFlow<AddPlantUiState> = _uiState.asStateFlow()

    private var currentPlant: Plant? = null
    private var _currentCareTasks: List<CareTask> = emptyList()
    val currentCareTasks: List<CareTask> get() = _currentCareTasks

    fun loadPlant(plantId: Long?) {
        viewModelScope.launch {
            if (plantId == null) {
                currentPlant = Plant(
                    id = 0,
                    name = "",
                    imageUri = "",
                    createdAt = System.currentTimeMillis()
                )
                _currentCareTasks = emptyList()
                _uiState.value = AddPlantUiState.NewPlant
            } else {
                mainRepository.getPlantById(plantId)?.let { plant ->
                    currentPlant = plant
                    // TODO: Получить задачи из репозитория
                    _currentCareTasks = emptyList()
                    _uiState.value = AddPlantUiState.EditPlant(plant)
                } ?: run {
                    _uiState.value = AddPlantUiState.Error("Растение не найдено")
                }
            }
        }
    }

    fun updatePlantName(name: String) {
        currentPlant = currentPlant?.copy(name = name)
        updateUiState()
    }

    fun updatePlantImage(imageUri: String) {
        currentPlant = currentPlant?.copy(imageUri = imageUri)
        updateUiState()
    }

    fun addCareTask(task: CareTask) {
        _currentCareTasks = _currentCareTasks + task
        updateUiState()
    }

    fun removeCareTask(index: Int) {
        _currentCareTasks = _currentCareTasks.toMutableList().apply { removeAt(index) }
        updateUiState()
    }

    fun savePlant() {
        viewModelScope.launch {
            currentPlant?.let { plant ->
                if (plant.id == 0L) {
                    // Создание нового растения
                    mainRepository.createPlant(plant, _currentCareTasks)
                } else {
                    // Обновление существующего растения
                    mainRepository.updatePlant(plant, _currentCareTasks)
                }
            }
        }
    }

    private fun updateUiState() {
        currentPlant?.let { plant ->
            _uiState.value = AddPlantUiState.EditPlant(plant)
        }
    }
}

sealed interface AddPlantUiState {
    data object Loading : AddPlantUiState
    data object NewPlant : AddPlantUiState
    data class EditPlant(val plant: Plant) : AddPlantUiState
    data class Error(val message: String) : AddPlantUiState
} 