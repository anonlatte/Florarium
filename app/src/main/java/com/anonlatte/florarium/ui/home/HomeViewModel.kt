package com.anonlatte.florarium.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.repository.MainRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val mainRepository = MainRepository(application)

    val plantsList: LiveData<List<Plant>> = liveData {
        val data = mainRepository.getPlants()
        emitSource(data)
    }
}
