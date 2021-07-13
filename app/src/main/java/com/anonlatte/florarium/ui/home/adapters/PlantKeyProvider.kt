package com.anonlatte.florarium.ui.home.adapters

import androidx.recyclerview.selection.ItemKeyProvider

class PlantKeyProvider(
    private val adapter: PlantsAdapter
) : ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long = adapter.currentList[position].plant.plantId
    override fun getPosition(key: Long): Int {
        return adapter.currentList.indexOfFirst { it.plant.plantId == key }
    }
}
