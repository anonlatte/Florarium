package com.anonlatte.florarium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anonlatte.florarium.databinding.ListItemPlantBinding
import com.anonlatte.florarium.db.models.Plant

class PlantsAdapter(var plantsList: List<Plant>) :
    RecyclerView.Adapter<PlantsAdapter.PlantsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemPlantBinding.inflate(inflater)
        return PlantsViewHolder(binding)
    }

    override fun getItemCount(): Int = plantsList.size

    override fun onBindViewHolder(holder: PlantsViewHolder, position: Int) =
        holder.bind(plantsList[position])

    inner class PlantsViewHolder(private val binding: ListItemPlantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            binding.plant = plant
            binding.executePendingBindings()
        }
    }
}