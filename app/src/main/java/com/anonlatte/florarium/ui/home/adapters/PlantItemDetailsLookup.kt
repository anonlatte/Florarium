package com.anonlatte.florarium.ui.home.adapters

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class PlantItemDetailsLookup(
    private val recyclerView: RecyclerView
) : ItemDetailsLookup<Long>() {

    override fun getItemDetails(
        event: MotionEvent
    ): ItemDetails<Long>? {
        return recyclerView.findChildViewUnder(event.x, event.y)?.let {
            (recyclerView.getChildViewHolder(it) as? PlantsAdapter.PlantsViewHolder)?.getItemDetail()
        }
    }
}
