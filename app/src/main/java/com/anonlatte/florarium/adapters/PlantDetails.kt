package com.anonlatte.florarium.adapters

import androidx.recyclerview.selection.ItemDetailsLookup

class PlantDetails(private val adapterPosition: Int, private val selectedKey: Long?) :
    ItemDetailsLookup.ItemDetails<Long>() {
    override fun getSelectionKey() = selectedKey
    override fun getPosition() = adapterPosition
}
