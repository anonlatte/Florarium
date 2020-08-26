package com.anonlatte.florarium.adapters

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class PlantItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Long>() {

    override fun getItemDetails(event: MotionEvent) =
        recyclerView.findChildViewUnder(event.x, event.y)?.let {
            (recyclerView.getChildViewHolder(it) as? PlantsAdapter.PlantsViewHolder)
                ?.getItemDetail()
        }
}
