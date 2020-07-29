package com.anonlatte.florarium.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anonlatte.florarium.db.models.Plant

class HomeViewModel : ViewModel() {
    // TODO init value
    var plantsList = MutableLiveData<List<Plant>>(emptyList())
}
