package com.anonlatte.florarium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anonlatte.florarium.databinding.ListItemPlantBinding
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.utilities.TimeStampHelper

class PlantsAdapter :
    RecyclerView.Adapter<PlantsAdapter.PlantsViewHolder>() {
    private var plantsList = emptyList<Plant>()
    private var scheduleList = emptyList<RegularSchedule>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemPlantBinding.inflate(inflater)
        return PlantsViewHolder(binding)
    }

    override fun getItemCount(): Int = plantsList.size

    override fun onBindViewHolder(holder: PlantsViewHolder, position: Int) =
        holder.bind(plantsList[position])

    internal fun setPlants(plants: List<Plant>) {
        plantsList = plants
        notifyDataSetChanged()
    }

    internal fun setSchedules(schedules: List<RegularSchedule>) {
        scheduleList = schedules
        notifyDataSetChanged()
    }

    inner class PlantsViewHolder(private val binding: ListItemPlantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant?) {
            plant?.let {
                binding.plant = plant
                binding.schedule = getFormattedSchedule(plant)
                binding.executePendingBindings()
            }
        }
    }

    // TODO beatify, visualize and improve UX.
    // Only for temporary realization
    private fun getFormattedSchedule(plant: Plant): String? {
        val schedule = scheduleList.firstOrNull { it.plantId == plant.plantId }
        schedule?.run {
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
        return null
    }
}
