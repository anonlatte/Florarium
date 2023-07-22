package com.anonlatte.florarium.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anonlatte.florarium.data.repository.IMainRepository
import com.anonlatte.florarium.ui.settings.data.SettingsCommand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val mainRepository: IMainRepository,
) : ViewModel() {

    private val _screenCommand = MutableSharedFlow<SettingsCommand>()
    val screenCommand: SharedFlow<SettingsCommand> = _screenCommand.asSharedFlow()

    fun updateNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            kotlin.runCatching {
                mainRepository.updateGlobalNotificationTime(hour, minute)
            }.onSuccess {
                _screenCommand.emit(SettingsCommand.SuccessTimeUpdated)
            }.onFailure {
                _screenCommand.emit(SettingsCommand.ErrorTimeUpdated)
            }
        }
    }
}

