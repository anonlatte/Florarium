package com.anonlatte.florarium.ui.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anonlatte.florarium.R
import com.anonlatte.florarium.databinding.FragmentPlantCreationBinding

class CreationFragment : Fragment() {
    private val viewModel by viewModels<CreationViewModel>()
    private lateinit var binding: FragmentPlantCreationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlantCreationBinding.inflate(inflater, container, false)

        makeScheduleItemsClickable()

        return binding.root
    }

    private fun makeScheduleItemsClickable() {
        TooltipCompat.setTooltipText(
            binding.wateringListItem.scheduleItem,
            getString(R.string.tooltip_watering)
        )
        TooltipCompat.setTooltipText(
            binding.sprayingListItem.scheduleItem,
            getString(R.string.tooltip_spraying)
        )
        TooltipCompat.setTooltipText(
            binding.fertilizingListItem.scheduleItem,
            getString(R.string.tooltip_fertilizing)
        )
        TooltipCompat.setTooltipText(
            binding.rotatingListItem.scheduleItem,
            getString(R.string.tooltip_rotating)
        )
    }
}