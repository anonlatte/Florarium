package com.anonlatte.florarium.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val mainRepository = MainRepository(application)

    val plantsList: LiveData<List<Plant>> = liveData {
        val data = mainRepository.getPlants()
        emitSource(data)
    }

    val regularSchedulesList: LiveData<List<RegularSchedule>> = liveData {
        val data = mainRepository.getRegularScheduleList()
        emitSource(data)
    }

    suspend fun deletePlants(plants: List<Plant>?): LiveData<Int> {
        return if (plants != null) {
            withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                MutableLiveData(mainRepository.deletePlants(plants))
            }
        } else {
            MutableLiveData(0)
        }
    }
}
