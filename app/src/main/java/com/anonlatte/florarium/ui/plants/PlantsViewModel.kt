package com.anonlatte.florarium.ui.plants

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.repository.MainRepository

class PlantsViewModel(application: Application) : AndroidViewModel(application) {
    private val mainRepository = MainRepository(application)

    val plantsList: LiveData<List<Plant>> = liveData {
        val data = mainRepository.getPlants()
        emitSource(data)
    }

    val regularSchedulesList: LiveData<List<RegularSchedule>> = liveData {
        val data = mainRepository.getRegularScheduleList()
        emitSource(data)
    }
}
