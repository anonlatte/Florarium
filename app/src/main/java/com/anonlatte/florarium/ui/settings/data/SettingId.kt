package com.anonlatte.florarium.ui.settings.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anonlatte.florarium.R

/**
 * Represents a setting link for navigation.
 */
enum class SettingId(@StringRes val title: Int, @DrawableRes val icon: Int) {
    NOTIFICATIONS(title = R.string.setting_notifications, icon = R.drawable.ic_notifications),
}