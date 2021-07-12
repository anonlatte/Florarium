package com.anonlatte.florarium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anonlatte.florarium.data.model.PlantWithSchedule
import com.anonlatte.florarium.databinding.ListItemPlantBinding
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.utilities.TimeStampHelper

class PlantsAdapter(
    private val onPlantClick: (Plant, RegularSchedule?) -> Unit
) : ListAdapter<PlantWithSchedule, PlantsAdapter.PlantsViewHolder>(DiffCallback()) {
    private var selectionTracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantsViewHolder {
        val binding = ListItemPlantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlantsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantsViewHolder, position: Int) {
        val item = getItem(position)
        if (selectionTracker != null) {
            holder.bind(item, selectionTracker!!.isSelected(position.toLong()))
        }
        holder.itemView.setOnClickListener {
            onPlantClick(item.plant, item.schedule)
        }
    }

    internal fun setTracker(tracker: SelectionTracker<Long>) {
        selectionTracker = tracker
    }

    class DiffCallback : DiffUtil.ItemCallback<PlantWithSchedule>() {
        override fun areItemsTheSame(
            oldItem: PlantWithSchedule,
            newItem: PlantWithSchedule
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PlantWithSchedule,
            newItem: PlantWithSchedule
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class PlantsViewHolder(private val binding: ListItemPlantBinding) :
        RecyclerView.ViewHolder(binding.root), ViewHolderWithDetails<Long> {
        fun bind(plantWithSchedule: PlantWithSchedule, isActivated: Boolean = false) {
            with(binding) {
                root.isActivated = isActivated
                plantImage.load(plantWithSchedule.plant.imageUrl)
                plantImage.contentDescription = plantWithSchedule.plant.name
                plantName.text = plantWithSchedule.plant.name
                plantDescription.text = getFormattedSchedule(plantWithSchedule.schedule)
            }
        }

        override fun getItemDetail(): ItemDetailsLookup.ItemDetails<Long> =
            PlantDetails(adapterPosition, itemId)
    }

    // TODO beatify, visualize and improve UX.
    // Only for temporary realization
    private fun getFormattedSchedule(schedule: RegularSchedule): String {
        schedule.run {
            val wateringSchedule = if (wateringInterval != null) {
                if (wateredAt != null) {
                    val lastCare = TimeStampHelper.getDaysFromTimestampAgo(
                        wateredAt
                    )
                    "${wateringInterval!! - lastCare}:$wateringInterval"
                } else {
                    wateringInterval.toString()
                }
            } else {
                ""
            }
            val sprayingSchedule = if (sprayingInterval != null) {
                if (sprayedAt != null) {
                    val lastCare = TimeStampHelper.getDaysFromTimestampAgo(
                        sprayedAt
                    )
                    " ${sprayingInterval!! - lastCare}:$sprayingInterval"
                } else {
                    sprayingInterval.toString()
                }
            } else {
                ""
            }

            val fertilizingSchedule = if (fertilizingInterval != null) {
                if (fertilizedAt != null) {
                    val lastCare = TimeStampHelper.getDaysFromTimestampAgo(
                        fertilizedAt
                    )
                    " ${fertilizingInterval!! - lastCare}:$fertilizingInterval"
                } else {
                    fertilizingInterval.toString()
                }
            } else {
                ""
            }

            val rotatingSchedule = if (rotatingInterval != null) {
                if (rotatedAt != null) {
                    val lastCare = TimeStampHelper.getDaysFromTimestampAgo(
                        rotatedAt
                    )
                    " ${rotatingInterval!! - lastCare}:$rotatingInterval"
                } else {
                    rotatingInterval.toString()
                }
            } else {
                ""
            }
            return wateringSchedule + sprayingSchedule + fertilizingSchedule + rotatingSchedule
        }
    }
}
