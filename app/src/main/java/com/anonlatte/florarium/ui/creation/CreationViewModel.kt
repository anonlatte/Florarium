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
import java.util.Date

class CreationViewModel(application: Application) : AndroidViewModel(application) {
    var plant: Plant = Plant()
    var regularSchedule: RegularSchedule = RegularSchedule()
    var winterSchedule: WinterSchedule = WinterSchedule()
    private val mainRepository = MainRepository(application)

    fun addPlantToGarden() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.createPlant(plant).also { plantId ->
                regularSchedule.plantId = plantId
                winterSchedule.plantId = plantId
            }
            addSchedule()
        }
    }

    private fun addSchedule() {
        mainRepository.addSchedule(regularSchedule, winterSchedule)
    }

    fun getTimestampFromDaysAgo(days: Int): Long =
        Date(System.currentTimeMillis() - (days * DAY_IN_MILLIS)).time

    fun getDaysFromTimestampAgo(timestamp: Long?): Int {
        return if (timestamp != null) {
            ((System.currentTimeMillis() - timestamp) / DAY_IN_MILLIS).toInt()
        } else {
            0
        }
    }

    companion object {
        const val DAY_IN_MILLIS = (1000 * 60 * 60 * 24).toLong()
    }
}
