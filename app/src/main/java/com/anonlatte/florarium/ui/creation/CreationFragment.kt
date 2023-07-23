package com.anonlatte.florarium.ui.creation

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.anonlatte.florarium.R
import com.anonlatte.florarium.app.utils.getDaysFromTimestampAgo
import com.anonlatte.florarium.app.utils.photo.PhotoPicker
import com.anonlatte.florarium.app.utils.photo.PhotoTaker
import com.anonlatte.florarium.data.domain.CareHolder
import com.anonlatte.florarium.data.domain.RegularSchedule
import com.anonlatte.florarium.data.domain.ScheduleType
import com.anonlatte.florarium.databinding.BottomSheetBinding
import com.anonlatte.florarium.databinding.FragmentPlantCreationBinding
import com.anonlatte.florarium.extensions.collectWithLifecycle
import com.anonlatte.florarium.extensions.setIcon
import com.anonlatte.florarium.ui.custom.CareScheduleItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.annotations.TestOnly
import timber.log.Timber


@AndroidEntryPoint
class CreationFragment : Fragment() {
    private var _binding: FragmentPlantCreationBinding? = null
    private val binding get() = _binding!!

    private val navArgs: CreationFragmentArgs by navArgs()

    /** Allows to pick photos from gallery */
    private val photoPicker = PhotoPicker(this, ::updatePlantImage)

    /** Takes photos from camera */
    private val photoTaker = PhotoTaker(this, ::updatePlantImage)

    private val careScheduleItemsUiData = ScheduleType.values().associate {
        when (it) {
            ScheduleType.WATERING -> ScheduleType.WATERING to CareScheduleItemData(
                title = R.string.title_watering,
                icon = R.drawable.ic_outline_drop_24,
                scheduleItemType = it
            )

            ScheduleType.SPRAYING -> ScheduleType.SPRAYING to CareScheduleItemData(
                title = R.string.title_spraying,
                icon = R.drawable.ic_outline_spray_24,
                scheduleItemType = it
            )

            ScheduleType.FERTILIZING -> ScheduleType.FERTILIZING to CareScheduleItemData(
                title = R.string.title_fertilizing,
                icon = R.drawable.ic_outline_fertilizing_24,
                scheduleItemType = it
            )

            ScheduleType.ROTATING -> ScheduleType.ROTATING to CareScheduleItemData(
                title = R.string.title_rotating,
                icon = R.drawable.ic_outline_rotate_right_24,
                scheduleItemType = it
            )
        }
    }


    private val viewModel by viewModels<CreationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlantCreationBinding.inflate(inflater, container, false)
        viewModel.restoreData(navArgs.plantToSchedule)
        initViews()
        subscribeUi()
        addImageButtonTooltip()

        return binding.root
    }

    private fun initViews() = with(binding) {
        btnAddPlant.setOnClickListener {
            viewModel.addPlantToGarden()
        }
        etTitle.doOnTextChanged { text, _, _, _ ->
            viewModel.setPlantName(text)
            when {
                text.isNullOrEmpty() -> {
                    tilTitle.error = getString(R.string.error_empty_plant_name)
                }

                text.length > tilTitle.counterMaxLength -> {
                    tilTitle.isCounterEnabled = true
                }

                else -> {
                    tilTitle.error = null
                    tilTitle.isCounterEnabled = false
                }
            }
        }
        btnLoadImage.setOnClickListener { showImageSelectDialog() }
        careScheduleItemsUiData.forEach { scheduleToData ->
            when (scheduleToData.key) {
                ScheduleType.WATERING -> wateringListItem
                ScheduleType.SPRAYING -> sprayingListItem
                ScheduleType.FERTILIZING -> fertilizingListItem
                ScheduleType.ROTATING -> rotatingListItem
            }.let { careScheduleItem ->
                careScheduleItem.updateData(scheduleToData.value)
                setScheduleItemListener(careScheduleItem)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (navArgs.plantToSchedule != null) {
            binding.btnAddPlant.apply {
                setText(R.string.btn_save)
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_outline_save_24)
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.revokeData()
    }

    private fun subscribeUi() {
        collectMainState()
        collectCommands()
        collectImageCompressionFlow()
    }

    private fun collectImageCompressionFlow() {
        photoPicker.imageCompressionFlow.collectWithLifecycle(this) { isCompressionInProgress ->
            binding.pbImageLoading.isVisible = isCompressionInProgress
        }
        photoTaker.imageCompressionFlow.collectWithLifecycle(this) { isCompressionInProgress ->
            binding.pbImageLoading.isVisible = isCompressionInProgress
        }
    }

    private fun collectMainState() {
        viewModel.plantCreationState.collectWithLifecycle(this) { state ->
            binding.progressCreation.isVisible = state is PlantCreationState.Loading
            if (state !is PlantCreationError) {
                binding.tilTitle.error = null
            }
            when (state) {
                is PlantCreationState.Success -> {
                    onPlantCreated()
                }

                PlantCreationState.Loading -> {
                    Unit
                }

                PlantCreationState.Idle -> {
                    Unit
                }

                PlantCreationError.NameIsEmpty -> {
                    binding.tilTitle.error = getString(R.string.error_empty_plant_name)
                }

                PlantCreationError.CouldNotCreatePlant -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_could_not_create_plant),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                PlantCreationError.NameIsTooLong -> {
                    binding.tilTitle.error = getString(R.string.error_long_plant_name)
                }

                is PlantCreationState.PlantRecreation -> {
                    if (state.data.plant.imageUri.isNotEmpty()) {
                        binding.plantImageView.load(Uri.parse(state.data.plant.imageUri)) {
                            listener(
                                onError = { _, errorResult ->
                                    Timber.e(errorResult.throwable)
                                }
                            )
                        }
                    }
                    if (state.data.plant.name.isNotEmpty()) {
                        binding.etTitle.setText(state.data.plant.name)
                    }
                    restoreCareSchedule(state.data.schedule, state.data.careHolder)
                }
            }
        }
    }

    private fun collectCommands() {
        viewModel.uiCommand.collectWithLifecycle(viewLifecycleOwner) { command ->
            when (command) {
                is PlantCreationCommand.OpenScheduleScreen -> {
                    openScheduleDialog(
                        schedule = command.schedule,
                        careHolder = command.careHolder,
                        scheduleType = command.scheduleItemType,
                        title = command.title,
                        icon = command.icon,
                    )
                }

                PlantCreationCommand.PlantCreated -> {
                    onPlantCreated()
                }
            }
        }
    }

    private fun onPlantCreated() {
        Toast.makeText(
            requireContext(),
            getString(R.string.message_plant_is_added),
            Toast.LENGTH_SHORT
        ).show()
        findNavController().navigateUp()
    }

    /** Called if user didn't submit the form */
    private fun clearCreatedImageFiles() {
        photoPicker.clearCreatedImageFiles()
        photoTaker.clearCreatedImageFiles()
    }

    private fun restoreCareSchedule(regularSchedule: RegularSchedule, careHolder: CareHolder) {
        with(binding) {
            wateringListItem.updateData {
                it.copy(
                    intervalValue = regularSchedule.wateringInterval,
                    lastCareValue = getDaysFromTimestampAgo(careHolder.wateredAt)
                )
            }
            sprayingListItem.updateData {
                it.copy(
                    intervalValue = regularSchedule.sprayingInterval,
                    lastCareValue = getDaysFromTimestampAgo(careHolder.sprayedAt)
                )
            }
            fertilizingListItem.updateData {
                it.copy(
                    intervalValue = regularSchedule.fertilizingInterval,
                    lastCareValue = getDaysFromTimestampAgo(careHolder.fertilizedAt)
                )
            }
            rotatingListItem.updateData {
                it.copy(
                    intervalValue = regularSchedule.rotatingInterval,
                    lastCareValue = getDaysFromTimestampAgo(careHolder.rotatedAt)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!viewModel.keepCreatedImageFiles) clearCreatedImageFiles()
        _binding = null
    }

    private fun updatePlantImage(photoUri: Uri?) {
        if (photoUri != null) {
            viewModel.updatePlantImage(photoUri.toString())
            binding.plantImageView.load(photoUri) {
                listener(
                    onError = { _, errorResult ->
                        Timber.e(errorResult.throwable)
                    }
                )
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.error_image_not_saved),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showImageSelectDialog() {
        val multiItems = ImageRetrieveType.getRetrieveWaysText(requireContext())
        val performActionByRetrieveType = { retrieveTypeIndex: Int ->
            when (ImageRetrieveType.values()[retrieveTypeIndex]) {
                ImageRetrieveType.CAMERA -> {
                    photoPicker.clearCreatedImageFiles()
                    photoTaker.takePhoto()
                }

                ImageRetrieveType.GALLERY -> {
                    photoTaker.clearCreatedImageFiles()
                    photoPicker.pickPhoto()
                }
            }
        }
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.title_intent_image_select))
            setItems(multiItems) { dialog, retrieveTypeIndex ->
                performActionByRetrieveType(retrieveTypeIndex)
                dialog.dismiss()
            }
        }.show()
    }

    private fun openScheduleDialog(
        schedule: RegularSchedule,
        careHolder: CareHolder,
        scheduleType: ScheduleType,
        title: Int,
        icon: Int,
    ) {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = BottomSheetBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)

        dialogBinding.bottomSheetTitle.text = getString(title)
        dialogBinding.bottomSheetTitle.setIcon(
            left = ContextCompat.getDrawable(requireContext(), icon)
        )

        restoreCareScheduleItem(schedule, careHolder, dialogBinding, scheduleType)
        setDialogListeners(dialog, dialogBinding, scheduleType)

        dialog.show()
    }

    private fun setDialogListeners(
        dialog: BottomSheetDialog,
        dialogBinding: BottomSheetBinding,
        scheduleType: ScheduleType,
    ) {
        dialog.setOnDismissListener {
            /**
             * TODO check if DefaultCareValue slider < 0 then don't execute
             *  move out checking condition from [updateCareSchedule]
             */
            // fixme repeating code
            val careScheduleItem = when (scheduleType) {
                ScheduleType.WATERING -> binding.wateringListItem
                ScheduleType.SPRAYING -> binding.sprayingListItem
                ScheduleType.FERTILIZING -> binding.fertilizingListItem
                ScheduleType.ROTATING -> binding.rotatingListItem
            }
            updateCareSchedule(
                dialogBinding,
                careScheduleItem
            )
        }
    }

    private fun onScheduleItemClickListener(careScheduleItem: CareScheduleItem) {
        viewModel.onScheduleItemClickListener(careScheduleItem.state)
    }

    private fun setScheduleItemListener(careScheduleItem: CareScheduleItem) {
        with(careScheduleItem) {
            setOnClickListener {
                onScheduleItemClickListener(careScheduleItem)
            }
            /** Convert itemSwitch to View to avoid overriding [View.performClick] */
            careScheduleItem.setOnSwitchCheckedChangeListener { isChecked ->
                if (isChecked) {
                    onScheduleItemClickListener(careScheduleItem)
                } else {
                    clearScheduleField(state.scheduleItemType)
                }
            }
        }
    }

    private fun clearScheduleField(scheduleItemType: ScheduleType) {
        viewModel.clearScheduleField(scheduleItemType)
        val careScheduleItem = when (scheduleItemType) {
            ScheduleType.WATERING -> binding.wateringListItem
            ScheduleType.SPRAYING -> binding.sprayingListItem
            ScheduleType.FERTILIZING -> binding.fertilizingListItem
            ScheduleType.ROTATING -> binding.rotatingListItem
        }
        careScheduleItem.updateData {
            it.copy(intervalValue = 0)
        }
    }

    private fun restoreCareScheduleItem(
        regularSchedule: RegularSchedule,
        careHolder: CareHolder,
        dialogBinding: BottomSheetBinding,
        careScheduleItem: ScheduleType,
    ) {
        var defaultIntervalValue = 0
        var lastCareIntervalValue = 0

        when (careScheduleItem) {
            ScheduleType.WATERING -> binding.wateringListItem.apply {
                defaultIntervalValue = regularSchedule.wateringInterval
                lastCareIntervalValue = getDaysFromTimestampAgo(careHolder.wateredAt)
            }

            ScheduleType.SPRAYING -> binding.sprayingListItem.apply {
                defaultIntervalValue = regularSchedule.sprayingInterval
                lastCareIntervalValue = getDaysFromTimestampAgo(careHolder.sprayedAt)
            }

            ScheduleType.FERTILIZING -> binding.fertilizingListItem.apply {
                defaultIntervalValue = regularSchedule.fertilizingInterval
                lastCareIntervalValue = getDaysFromTimestampAgo(careHolder.fertilizedAt)
            }

            ScheduleType.ROTATING -> binding.rotatingListItem.apply {
                defaultIntervalValue = regularSchedule.rotatingInterval
                lastCareIntervalValue = getDaysFromTimestampAgo(careHolder.rotatedAt)
            }

        }
        with(dialogBinding) {
            defaultIntervalItem.setTitle(R.string.title_interval_in_days, defaultIntervalValue)
            defaultIntervalItem.setSliderValue(defaultIntervalValue.toFloat())

            lastCareItem.setTitle(R.string.title_last_care, lastCareIntervalValue)
            lastCareItem.setSliderValue(lastCareIntervalValue.toFloat())
        }
    }

    private fun updateCareSchedule(
        dialogBinding: BottomSheetBinding,
        careScheduleItem: CareScheduleItem,
    ) {
        val defaultIntervalValue = dialogBinding.defaultIntervalItem.getSliderValue().toInt()
        careScheduleItem.updateData {
            it.copy(intervalValue = defaultIntervalValue)
        }


        val lastCareValue = dialogBinding.lastCareItem.getSliderValue().toInt()

        careScheduleItem.updateData {
            it.copy(intervalValue = defaultIntervalValue, lastCareValue = lastCareValue)
        }

        viewModel.updateSchedule(
            scheduleItemType = careScheduleItem.state.scheduleItemType,
            defaultIntervalValue = defaultIntervalValue,
            lastCareValue = lastCareValue
        )
    }

    private fun addImageButtonTooltip() {
        TooltipCompat.setTooltipText(
            binding.btnLoadImage,
            getString(R.string.btn_load_image)
        )
    }

    /** Returns max field length */
    @TestOnly
    fun getTitleInputLayoutMaxLength(): Int = binding.tilTitle.counterMaxLength
}

