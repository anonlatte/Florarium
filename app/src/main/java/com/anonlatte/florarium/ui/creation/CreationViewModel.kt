package com.anonlatte.florarium.ui.creation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.db.models.WinterSchedule
import com.anonlatte.florarium.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreationViewModel(application: Application) : AndroidViewModel(application) {
    var plant: Plant = Plant()
    var regularSchedule: RegularSchedule = RegularSchedule()
    var winterSchedule: WinterSchedule = WinterSchedule()
    private val mainRepository = MainRepository(application)
    var isPlantExist = false

    fun addPlantToGarden() {
        if (!isPlantExist) {
            viewModelScope.launch(Dispatchers.IO) {
                mainRepository.createPlant(plant).also { plantId ->
                    regularSchedule.plantId = plantId
                    winterSchedule.plantId = plantId
                }
                addSchedule()
            }
        } else {
            updatePlant()
        }
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
}
