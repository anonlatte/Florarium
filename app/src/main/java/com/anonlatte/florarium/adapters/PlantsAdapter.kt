package com.anonlatte.florarium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anonlatte.florarium.databinding.ListItemPlantBinding
import com.anonlatte.florarium.db.models.Plant

class PlantsAdapter :
    RecyclerView.Adapter<PlantsAdapter.PlantsViewHolder>() {
    private var plantsList = emptyList<Plant>()

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

    inner class PlantsViewHolder(private val binding: ListItemPlantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant?) {
            plant?.let {
                binding.plant = plant
                binding.executePendingBindings()
            }
        }
    }
}
