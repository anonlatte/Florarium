package com.anonlatte.florarium.ui.settings.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import com.anonlatte.florarium.databinding.ListItemSettingHeaderBinding
import com.anonlatte.florarium.ui.settings.data.SettingsItemElement

class SettingsHeaderViewHolder(
    private val binding: ListItemSettingHeaderBinding,
) : BaseViewHolder<SettingsItemElement.SettingsHeader>(binding.root) {

    override fun bind(item: SettingsItemElement.SettingsHeader) {
        binding.root.text = item.title
    }

    companion object {
        fun from(parent: ViewGroup) = SettingsHeaderViewHolder(
            ListItemSettingHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}