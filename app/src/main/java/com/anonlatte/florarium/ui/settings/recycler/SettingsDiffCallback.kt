package com.anonlatte.florarium.ui.settings.recycler

import androidx.recyclerview.widget.DiffUtil
import com.anonlatte.florarium.ui.settings.data.SettingsItemElement

class SettingsDiffCallback : DiffUtil.ItemCallback<SettingsItemElement>() {

    override fun areItemsTheSame(
        oldItem: SettingsItemElement,
        newItem: SettingsItemElement,
    ): Boolean = oldItem.areItemsTheSame(newItem)

    override fun areContentsTheSame(
        oldItem: SettingsItemElement,
        newItem: SettingsItemElement,
    ): Boolean = oldItem.areContentsTheSame(newItem)
}