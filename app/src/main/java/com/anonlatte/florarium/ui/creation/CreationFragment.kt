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
import kotlinx.android.synthetic.main.list_item_schedule.view.*

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
        scheduleItemView: View?,
        title: String?,
        icon: Drawable?
    ) {

        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = BottomSheetBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)

        dialogBinding.title = title
        dialogBinding.icon = icon

        setDialogListeners(dialog, dialogBinding, scheduleItemView)

        addSliderListeners(dialogBinding)

        dialog.show()

    }

    private fun setDialogListeners(
        dialog: BottomSheetDialog,
        dialogBinding: BottomSheetBinding,
        scheduleItemView: View?
    ) {
        dialogBinding.okButton.setOnClickListener {
            scheduleItemView?.itemSwitch!!.isChecked = true
            dialog.dismiss()
        }

        dialog.setOnCancelListener {
            scheduleItemView?.itemSwitch!!.isChecked = false
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
            scheduleItem.setOnClickListener { onScheduleItemClickListener(it, title, icon) }
            scheduleItem.itemSwitch.setOnTouchListener { switchView, event ->
                if (event.action == MotionEvent.ACTION_DOWN && !(switchView as Switch).isChecked) {
                    onScheduleItemClickListener(switchView, title, icon)
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