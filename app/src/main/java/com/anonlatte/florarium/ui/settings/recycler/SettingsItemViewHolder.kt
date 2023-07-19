package com.anonlatte.florarium.ui.settings.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import com.anonlatte.florarium.databinding.ListItemSettingBinding
import com.anonlatte.florarium.ui.settings.data.SettingId
import com.anonlatte.florarium.ui.settings.data.SettingsItemElement

class SettingsItemViewHolder(
    private val binding: ListItemSettingBinding,
    private val onSettingClick: (SettingId) -> Unit,
) : BaseViewHolder<SettingsItemElement.SettingsItem>(binding.root) {


    override fun bind(item: SettingsItemElement.SettingsItem) {
        binding.root.text = item.title
        binding.root.setCompoundDrawablesRelativeWithIntrinsicBounds(
            item.iconLeft,
            0,
            0,
            if (item.showChevron) binding.root.compoundPaddingEnd else 0
        )
        binding.root.setOnClickListener { onSettingClick(item.id) }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onSettingClick: (SettingId) -> Unit,
        ): SettingsItemViewHolder {
            return SettingsItemViewHolder(
                ListItemSettingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onSettingClick
            )
        }
    }
}