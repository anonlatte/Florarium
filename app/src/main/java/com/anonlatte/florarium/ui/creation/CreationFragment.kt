package com.anonlatte.florarium.ui.creation

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anonlatte.florarium.R
import com.anonlatte.florarium.databinding.BottomSheetBinding
import com.anonlatte.florarium.databinding.FragmentPlantCreationBinding
import com.anonlatte.florarium.databinding.ListItemScheduleBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class CreationFragment : Fragment() {
    private val viewModel by viewModels<CreationViewModel>()
    private lateinit var binding: FragmentPlantCreationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlantCreationBinding.inflate(inflater, container, false)

        bindScheduleItemClickListener(binding.wateringListItem)
        bindScheduleItemClickListener(binding.sprayingListItem)
        bindScheduleItemClickListener(binding.fertilizingListItem)
        bindScheduleItemClickListener(binding.rotatingListItem)

        makeScheduleItemsClickable()

        return binding.root
    }

    @SuppressLint("InflateParams")
    private fun onScheduleItemClickListener(
        title: String?,
        icon: Drawable?
    ) = View.OnClickListener {

        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = BottomSheetBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.title = title
        dialogBinding.icon = icon
    }

    private fun bindScheduleItemClickListener(itemScheduleBinding: ListItemScheduleBinding) {
        with(itemScheduleBinding) {
            executePendingBindings()
            scheduleItem.setOnClickListener(onScheduleItemClickListener(title, icon))
        }
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