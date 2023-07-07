package com.anonlatte.florarium.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.data.repository.IMainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: IMainRepository
) : ViewModel() {

    private val _screenState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val screenState: StateFlow<HomeScreenState> = _screenState.asStateFlow()
    fun getPlantsToSchedules() {
        viewModelScope.launch {
            _screenState.emit(HomeScreenState.Default(mainRepository.getPlantsToSchedules()))
        }
    }
}

