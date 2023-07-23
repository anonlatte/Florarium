package com.anonlatte.florarium.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.anonlatte.florarium.app.utils.TimeStampHelper.getDaysFromTimestampAgo
import com.anonlatte.florarium.data.domain.CareHolder
import com.anonlatte.florarium.data.domain.PlantWithSchedule
import com.anonlatte.florarium.data.domain.RegularSchedule
import com.anonlatte.florarium.databinding.ListItemPlantBinding

class PlantsAdapter(
    private val onPlantClick: (PlantWithSchedule) -> Unit,
) : ListAdapter<PlantWithSchedule, PlantsAdapter.PlantsViewHolder>(DiffCallback()) {

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
            onPlantClick(item)
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
                plantImage.isVisible = plantWithSchedule.plant.imageUri.isNotEmpty()
                if (plantWithSchedule.plant.imageUri.isNotEmpty()) {
                    plantImage.load(plantWithSchedule.plant.imageUri) {
                        transformations(CircleCropTransformation())
                    }
                }
                plantImage.contentDescription = plantWithSchedule.plant.name
                plantName.text = plantWithSchedule.plant.name
                plantDescription.text =
                    getFormattedSchedule(plantWithSchedule.schedule, plantWithSchedule.careHolder)
            }
        }

        private fun getFormattedSchedule(
            schedule: RegularSchedule,
            careHolder: CareHolder,
        ): String {
            with(schedule) {
                return listOfNotNull(
                    formatSchedule(wateringInterval, careHolder.wateredAt),
                    formatSchedule(sprayingInterval, careHolder.sprayedAt),
                    formatSchedule(fertilizingInterval, careHolder.fertilizedAt),
                    formatSchedule(rotatingInterval, careHolder.rotatedAt)
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
                val lastCare = getDaysFromTimestampAgo(doneAt)
                "${interval - lastCare}:$interval"
            } else {
                interval.toString()
            }
        } else {
            null
        }
    }
}
