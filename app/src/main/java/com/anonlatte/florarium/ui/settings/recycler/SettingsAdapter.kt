package com.anonlatte.florarium.ui.settings.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anonlatte.florarium.ui.settings.data.SettingId
import com.anonlatte.florarium.ui.settings.data.SettingsItemElement
import com.anonlatte.florarium.ui.settings.data.SettingsItemElement.Companion.TYPE_HEADER
import com.anonlatte.florarium.ui.settings.data.SettingsItemElement.Companion.TYPE_ITEM

/**
 * Adapter for the settings list.
 * Supports two types of items:
 * - [SettingsItemElement.SettingsItem]
 * - [SettingsItemElement.SettingsHeader]
 * @param onSettingClick callback for setting click.
 */
class SettingsAdapter(
    private val onSettingClick: (SettingId) -> Unit,
) : ListAdapter<SettingsItemElement, RecyclerView.ViewHolder>(SettingsDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SettingsItemViewHolder -> holder.bind(item as SettingsItemElement.SettingsItem)
            is SettingsHeaderViewHolder -> holder.bind(item as SettingsItemElement.SettingsHeader)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> SettingsHeaderViewHolder.from(parent)
            TYPE_ITEM -> SettingsItemViewHolder.from(parent, onSettingClick)
            else -> error("Invalid view type")
        }
    }
}