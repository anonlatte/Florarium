package com.anonlatte.florarium.ui.settings.data

import androidx.annotation.DrawableRes

/**
 * Represents a setting item for the settings screen.
 * @see SettingsItemElement.SettingsHeader
 * @see SettingsItemElement.SettingsItem
 */
sealed interface SettingsItemElement {

    /**
     * Returns the type of the item.
     * @see TYPE_HEADER
     * @see TYPE_ITEM
     */
    val type: Int

    /** To be used by [androidx.recyclerview.widget.DiffUtil.ItemCallback.areItemsTheSame] */
    fun areItemsTheSame(newItem: SettingsItemElement): Boolean

    /** To be used by [androidx.recyclerview.widget.DiffUtil.ItemCallback.areContentsTheSame] */
    fun areContentsTheSame(newItem: SettingsItemElement): Boolean

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    data class SettingsHeader(val title: String) : SettingsItemElement {
        override val type: Int = TYPE_HEADER

        override fun areItemsTheSame(newItem: SettingsItemElement): Boolean {
            return newItem is SettingsHeader && this == newItem
        }

        override fun areContentsTheSame(newItem: SettingsItemElement): Boolean {
            return newItem is SettingsHeader && this == newItem
        }
    }

    data class SettingsItem(
        val id: SettingId,
        val title: String,
        @DrawableRes val iconLeft: Int,
        val showChevron: Boolean = true,
    ) : SettingsItemElement {
        override val type: Int = TYPE_ITEM

        override fun areItemsTheSame(newItem: SettingsItemElement): Boolean {
            return newItem is SettingsItem && this == newItem
        }

        override fun areContentsTheSame(newItem: SettingsItemElement): Boolean {
            return newItem is SettingsItem && this == newItem
        }
    }
}
