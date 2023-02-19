package com.anonlatte.florarium.ui.home.adapters

import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.anonlatte.florarium.app.utils.getDaysFromTimestampAgo
import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantWithSchedule
import com.anonlatte.florarium.data.model.RegularSchedule
import com.anonlatte.florarium.databinding.ListItemPlantBinding
import java.io.File

class PlantsAdapter(
    private val onPlantClick: (Plant, RegularSchedule?) -> Unit
) : ListAdapter<PlantWithSchedule, PlantsAdapter.PlantsViewHolder>(DiffCallback()) {
    // private var selectionTracker: SelectionTracker<Long>? = null

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
        holder.bind(item, false)
        holder.itemView.setOnClickListener {
            onPlantClick(item.plant, item.schedule)
        }
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

    class PlantsViewHolder(
        private val binding: ListItemPlantBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plantWithSchedule: PlantWithSchedule, isActivated: Boolean = false) {
            with(binding) {
                root.isActivated = isActivated
                plantImage.load(
                    File(
                        itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        plantWithSchedule.plant.imageUrl
                    )
                )
                plantImage.contentDescription = plantWithSchedule.plant.name
                plantName.text = plantWithSchedule.plant.name
                plantWithSchedule.schedule?.let { schedule ->
                    plantDescription.text = getFormattedSchedule(schedule)
                }
            }
        }

        private fun getFormattedSchedule(schedule: RegularSchedule): String {
            with(schedule) {
                return listOfNotNull(
                    formatSchedule(wateringInterval, wateredAt),
                    formatSchedule(sprayingInterval, sprayedAt),
                    formatSchedule(fertilizingInterval, fertilizedAt),
                    formatSchedule(rotatingInterval, rotatedAt)
                ).joinToString(" ")
            }
        }

        /**
         * Returns formatted schedule or null
         * variants:
         *  - daysLeft:interval
         *  - interval
         *  - null
         * */
        private fun formatSchedule(
            interval: Int?, doneAt: Long?
        ): String? = if (interval != null) {
            if (doneAt != null) {
                val lastCare = getDaysFromTimestampAgo(
                    doneAt
                )
                "${interval - lastCare}:$interval"
            } else {
                interval.toString()
            }
        } else {
            null
        }
    }
}
