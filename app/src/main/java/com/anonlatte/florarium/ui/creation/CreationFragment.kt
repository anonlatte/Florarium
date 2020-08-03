package com.anonlatte.florarium.ui.creation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.anonlatte.florarium.R
import com.anonlatte.florarium.databinding.BottomSheetBinding
import com.anonlatte.florarium.databinding.FragmentPlantCreationBinding
import com.anonlatte.florarium.databinding.ListItemScheduleBinding
import com.anonlatte.florarium.db.models.ScheduleType
import com.anonlatte.florarium.utilities.REQUEST_IMAGE_CAPTURE
import com.anonlatte.florarium.utilities.REQUEST_IMAGE_SELECT
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileDescriptor

class CreationFragment : Fragment() {
    private val viewModel by viewModels<CreationViewModel>()
    private var _binding: FragmentPlantCreationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantCreationBinding.inflate(inflater, container, false)
        _binding!!.viewModel = viewModel

        setListeners()
        makeScheduleItemsClickable()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_SELECT -> {
                    resultData?.data?.also { uri ->
                        lifecycleScope.launch {
                            val bitmap = getBitmapFromUri(uri)
                            binding.plantImageView.setImageBitmap(bitmap)
                        }
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    resultData?.let {
                        val imageBitmap = it.extras?.get("data") as Bitmap
                        binding.plantImageView.setImageBitmap(imageBitmap)
                    }
                }
            }
        }
    }

    // TODO add extended validation
    private fun setListeners() {
        binding.addPlantButton.setOnClickListener {
            if (binding.titleEditText.text.isNullOrEmpty()) {
                binding.titleInputLayout.error = getString(R.string.error_empty_plant_name)
            } else {
                binding.titleInputLayout.error = null
                viewModel.addPlantToGarden()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.message_plant_is_added),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
        binding.titleEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                binding.titleInputLayout.error = getString(R.string.error_empty_plant_name)
            } else {
                binding.titleInputLayout.error = null
            }
        }
        binding.loadImage.setOnClickListener {
            showIntentChooseDialog()
        }
        setScheduleItemListener(binding.wateringListItem)
        setScheduleItemListener(binding.sprayingListItem)
        setScheduleItemListener(binding.fertilizingListItem)
        setScheduleItemListener(binding.rotatingListItem)
    }

    private fun showIntentChooseDialog() {
        val multiItems = arrayOf("Camera", "Gallery")
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.title_intent_image_select))
            setItems(multiItems) { dialog, which ->
                if (which == 0) {
                    openImageCaptureIntent()
                } else {
                    openImageSelectIntent()
                }
                dialog.dismiss()
            }
            show()
        }
    }

    private fun openImageSelectIntent() {
        val imageSelectIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(imageSelectIntent, REQUEST_IMAGE_SELECT)
    }

    private fun openImageCaptureIntent() {
        val imageCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)
        }
        startActivityForResult(imageCaptureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private suspend fun getBitmapFromUri(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            requireActivity().contentResolver.openFileDescriptor(uri, "r")
        var image: Bitmap? = null
        parcelFileDescriptor?.use {
            val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
        }
        image
    }

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

        restoreCareSchedule(dialogBinding, itemScheduleBinding.scheduleItemType)
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
            updateCareSchedule(
                dialogBinding,
                itemScheduleBinding.scheduleItemType
            )
            dialog.dismiss()
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

    private fun setScheduleItemListener(itemScheduleBinding: ListItemScheduleBinding) {
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
                if (event.action == MotionEvent.ACTION_UP && !itemSwitch.isChecked) {
                    onScheduleItemClickListener(itemScheduleBinding, title, icon)
                    switchView.performClick()
                } else if (event.action == MotionEvent.ACTION_UP && itemSwitch.isChecked) {
                    clearScheduleFields(itemScheduleBinding.scheduleItemType)
                }
                event.actionMasked == MotionEvent.ACTION_MOVE
            }
        }
    }

    private fun restoreCareSchedule(dialogBinding: BottomSheetBinding, scheduleTypeValue: Int?) {
        with(viewModel) {
            when (ScheduleType.toScheduleType(scheduleTypeValue)) {
                ScheduleType.WATERING -> {
                    dialogBinding.careIntervalValue = regularSchedule.wateringInterval?.toFloat()
                    dialogBinding.winterCareIntervalValue =
                        winterSchedule.wateringInterval?.toFloat()
                    dialogBinding.lastCareValue =
                        getDaysFromTimestampAgo(regularSchedule.wateredAt).toFloat()
                }
                ScheduleType.SPRAYING -> {
                    dialogBinding.careIntervalValue = regularSchedule.sprayingInterval?.toFloat()
                    dialogBinding.winterCareIntervalValue =
                        winterSchedule.sprayingInterval?.toFloat()
                    dialogBinding.lastCareValue =
                        getDaysFromTimestampAgo(regularSchedule.sprayedAt).toFloat()
                }
                ScheduleType.FERTILIZING -> {
                    dialogBinding.careIntervalValue =
                        regularSchedule.fertilizingInterval?.toFloat()
                    dialogBinding.winterCareIntervalValue =
                        winterSchedule.fertilizingInterval?.toFloat()
                    dialogBinding.lastCareValue =
                        getDaysFromTimestampAgo(regularSchedule.fertilizedAt).toFloat()
                }
                ScheduleType.ROTATING -> {
                    dialogBinding.careIntervalValue =
                        regularSchedule.rotatingInterval?.toFloat()
                    dialogBinding.winterCareIntervalValue =
                        winterSchedule.rotatingInterval?.toFloat()
                    dialogBinding.lastCareValue =
                        getDaysFromTimestampAgo(regularSchedule.rotatedAt).toFloat()
                }
            }
        }
    }

    private fun updateCareSchedule(
        dialogBinding: BottomSheetBinding,
        scheduleTypeValue: Int?
    ) {
        val defaultIntervalValue = dialogBinding.defaultIntervalItem.daySlider.value.toInt()
        val winterIntervalValue = dialogBinding.winterIntervalItem.daySlider.value.toInt()
        val lastCareValue = dialogBinding.lastCareItem.daySlider.value.toInt()

        with(viewModel) {
            when (ScheduleType.toScheduleType(scheduleTypeValue)) {
                ScheduleType.WATERING -> {
                    regularSchedule.wateringInterval = defaultIntervalValue
                    regularSchedule.wateredAt = getTimestampFromDaysAgo(lastCareValue)
                    winterSchedule.wateringInterval = winterIntervalValue
                }
                ScheduleType.SPRAYING -> {
                    regularSchedule.sprayingInterval = defaultIntervalValue
                    regularSchedule.sprayedAt = getTimestampFromDaysAgo(lastCareValue)
                    winterSchedule.sprayingInterval = winterIntervalValue
                }
                ScheduleType.FERTILIZING -> {
                    regularSchedule.fertilizingInterval = defaultIntervalValue
                    regularSchedule.fertilizedAt = getTimestampFromDaysAgo(lastCareValue)
                    winterSchedule.fertilizingInterval = winterIntervalValue
                }
                ScheduleType.ROTATING -> {
                    regularSchedule.rotatingInterval = defaultIntervalValue
                    regularSchedule.rotatedAt = getTimestampFromDaysAgo(lastCareValue)
                    winterSchedule.rotatingInterval = winterIntervalValue
                }
            }
        }
    }

    private fun clearScheduleFields(scheduleTypeValue: Int?) {
        with(viewModel) {
            when (ScheduleType.toScheduleType(scheduleTypeValue)) {
                ScheduleType.WATERING -> {
                    regularSchedule.wateringInterval = null
                    winterSchedule.wateringInterval = null
                    regularSchedule.wateredAt = null
                }
                ScheduleType.SPRAYING -> {
                    regularSchedule.sprayingInterval = null
                    winterSchedule.sprayingInterval = null
                    regularSchedule.sprayedAt = null
                }
                ScheduleType.FERTILIZING -> {
                    regularSchedule.fertilizingInterval = null
                    winterSchedule.fertilizingInterval = null
                    regularSchedule.fertilizedAt = null
                }
                ScheduleType.ROTATING -> {
                    regularSchedule.rotatingInterval = null
                    winterSchedule.rotatingInterval = null
                    regularSchedule.rotatedAt = null
                }
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
