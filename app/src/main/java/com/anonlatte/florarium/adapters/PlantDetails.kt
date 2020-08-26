package com.anonlatte.florarium.adapters

import androidx.recyclerview.selection.ItemDetailsLookup
import com.anonlatte.florarium.db.models.Plant

class PlantDetails(private val adapterPosition: Int, private val selectedKey: Plant?) :
    ItemDetailsLookup.ItemDetails<Plant>() {
    override fun getSelectionKey() = selectedKey
    override fun getPosition() = adapterPosition
}
