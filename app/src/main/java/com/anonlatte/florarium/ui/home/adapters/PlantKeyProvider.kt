package com.anonlatte.florarium.ui.home.adapters

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class PlantKeyProvider(private val recyclerView: RecyclerView) :
    ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long? = recyclerView.adapter?.getItemId(position)
    override fun getPosition(key: Long): Int {
        return recyclerView.findViewHolderForItemId(
            key
        )?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}
