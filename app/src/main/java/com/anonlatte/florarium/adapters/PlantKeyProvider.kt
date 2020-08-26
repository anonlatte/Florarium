package com.anonlatte.florarium.adapters

import androidx.recyclerview.selection.ItemKeyProvider
import com.anonlatte.florarium.db.models.Plant

class PlantKeyProvider(private val items: List<Plant>) : ItemKeyProvider<Plant>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Plant? = items.getOrNull(position)
    override fun getPosition(key: Plant): Int = items.indexOf(key)
}
