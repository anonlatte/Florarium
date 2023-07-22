package com.anonlatte.florarium.ui.settings.data

sealed interface SettingsCommand {
    object SuccessTimeUpdated : SettingsCommand
    object ErrorTimeUpdated : SettingsCommand
}