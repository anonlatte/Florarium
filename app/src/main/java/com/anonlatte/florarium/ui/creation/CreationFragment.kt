package com.anonlatte.florarium.ui.creation

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
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
    private var _binding: FragmentPlantCreationBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainRepository: MainRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlantCreationBinding.inflate(inflater, container, false)

        mainRepository = MainRepository.getRepository(requireActivity().application)

        bindScheduleItemClickListener(binding.wateringListItem)
        bindScheduleItemClickListener(binding.sprayingListItem)
        bindScheduleItemClickListener(binding.fertilizingListItem)
        bindScheduleItemClickListener(binding.rotatingListItem)

        makeScheduleItemsClickable()

        return binding.root
    }

    @SuppressLint("InflateParams")
    private fun onScheduleItemClickListener(
        itemScheduleBinding: ListItemScheduleBinding,
        title: String?,
        icon: Drawable?
    ) {

        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = BottomSheetBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)

        dialogBinding.title = title
        dialogBinding.icon = icon

        setDialogListeners(dialog, dialogBinding, itemScheduleBinding)

        addSliderListeners(dialogBinding)

        dialog.show()

    }

    private fun setDialogListeners(
        dialog: BottomSheetDialog,
        dialogBinding: BottomSheetBinding,
        itemScheduleBinding: ListItemScheduleBinding
    ) {
        dialogBinding.okButton.setOnClickListener {
            itemScheduleBinding.itemSwitch.isChecked = true
            dialog.dismiss()
        }

        dialog.setOnCancelListener {
            itemScheduleBinding.itemSwitch.isChecked = false
        }

        dialogBinding.cancelButton.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun addSliderListeners(dialogBinding: BottomSheetBinding) {

        with(dialogBinding.defaultIntervalItem) {
            daySlider.addOnChangeListener { _, value, _ ->
                title = getString(R.string.title_interval_in_days, value.toInt())
            }
        }

        with(dialogBinding.winterIntervalItem) {
            daySlider.addOnChangeListener { _, value, _ ->
                title = getString(R.string.title_interval_for_winter, value.toInt())
            }
        }

        with(dialogBinding.lastCareItem) {
            daySlider.addOnChangeListener { _, value, _ ->
                title = getString(R.string.title_last_care, value.toInt())
            }
        }
    }

    private fun bindScheduleItemClickListener(itemScheduleBinding: ListItemScheduleBinding) {
        with(itemScheduleBinding) {
            executePendingBindings()
            scheduleItem.setOnClickListener {
                onScheduleItemClickListener(
                    itemScheduleBinding,
                    title,
                    icon
                )
            }
            /** Convert itemSwitch to View to avoid overriding [View.performClick] */
            (itemSwitch as View).setOnTouchListener { switchView, event ->
                if (event.action == MotionEvent.ACTION_DOWN && !(switchView as Switch).isChecked) {
                    onScheduleItemClickListener(itemScheduleBinding, title, icon)
                    switchView.performClick()
                }
                false

            }
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