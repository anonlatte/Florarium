package com.anonlatte.florarium.ui.creation

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.anonlatte.florarium.R
import com.anonlatte.florarium.app.utils.getDaysFromTimestampAgo
import com.anonlatte.florarium.app.utils.photo.PhotoPicker
import com.anonlatte.florarium.app.utils.photo.PhotoTaker
import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.data.model.RegularSchedule
import com.anonlatte.florarium.data.model.ScheduleType
import com.anonlatte.florarium.databinding.BottomSheetBinding
import com.anonlatte.florarium.databinding.FragmentPlantCreationBinding
import com.anonlatte.florarium.extensions.appComponent
import com.anonlatte.florarium.extensions.collectWithLifecycle
import com.anonlatte.florarium.extensions.setAlarm
import com.anonlatte.florarium.extensions.setIcon
import com.anonlatte.florarium.ui.custom.CareScheduleItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.annotations.TestOnly
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random


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


    @Inject
    lateinit var viewModel: CreationViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlantCreationBinding.inflate(inflater, container, false)
        reinitializeScreen()
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

    private fun reinitializeScreen() {
        navArgs.plant?.let { plant ->
            viewModel.restorePlant(plant)
            navArgs.schedule?.let { schedule ->
                // TODO if field is null then hide/show 'not set'
                viewModel.restoreSchedule(schedule)
                restoreCareSchedule(schedule)
            }
            binding.plantImageView.load(Uri.parse(plant.imageUri)) {
                listener(
                    onError = { _, errorResult ->
                        Timber.e(errorResult.throwable)
                    }
                )
            }
            plant.name.takeIf {
                it.isNotEmpty()
            }?.let(binding.etTitle::setText)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (navArgs.plant != null) {
            binding.btnAddPlant.apply {
                setText(R.string.btn_save)
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_outline_save_24)
            }
        }
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
        viewModel.plantCreationState.collectWithLifecycle(this) {
            binding.progressCreation.isVisible = it is PlantCreationState.Loading
            if (it !is PlantCreationError) {
                binding.tilTitle.error = null
            }
            when (it) {
                is PlantCreationState.Success -> {
                    createAlarms(it.plantCreationData.plant, it.plantCreationData.schedule)
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
            }
        }
    }

    private fun collectCommands() {
        viewModel.uiCommand.collectWithLifecycle(viewLifecycleOwner) {
            when (it) {
                is PlantCreationCommand.OpenScheduleScreen -> {
                    openScheduleDialog(
                        schedule = it.schedule,
                        scheduleType = it.scheduleItemType,
                        title = it.title,
                        icon = it.icon,
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

    private fun restoreCareSchedule(regularSchedule: RegularSchedule) {
        with(binding) {
            wateringListItem.setItemDescription(
                regularSchedule.wateringInterval,
                getDaysFromTimestampAgo(regularSchedule.wateredAt)
            )
            sprayingListItem.setItemDescription(
                regularSchedule.sprayingInterval,
                getDaysFromTimestampAgo(regularSchedule.sprayedAt)
            )
            fertilizingListItem.setItemDescription(
                regularSchedule.fertilizingInterval,
                getDaysFromTimestampAgo(regularSchedule.fertilizedAt)
            )
            rotatingListItem.setItemDescription(
                regularSchedule.rotatingInterval,
                getDaysFromTimestampAgo(regularSchedule.rotatedAt)

            )
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

    private fun createAlarms(plant: Plant, regularSchedule: RegularSchedule) {
        val scheduleMap = getScheduleMap(regularSchedule)

        scheduleMap.keys.forEach { scheduleType ->
            scheduleMap[scheduleType]?.get(0)?.let { interval ->
                val lastCare = scheduleMap[scheduleType]?.get(1)
                val randomRequestId = Random.nextLong()
                val plantAlarm = PlantAlarm(
                    requestId = randomRequestId,
                    plantName = plant.name,
                    eventTag = scheduleType.name.lowercase(),
                    interval = interval,
                    lastCare = lastCare
                ).also { plantAlarm ->
                    plantAlarm.setAlarm(
                        requireContext(),
                        plantAlarm
                    )
                }

                viewModel.addPlantAlarm(plantAlarm)
            }
        }
    }

    private fun getScheduleMap(
        regularSchedule: RegularSchedule,
    ): MutableMap<ScheduleType, List<Long?>> {
        with(regularSchedule) {
            val schedule = mutableMapOf(
                ScheduleType.WATERING to listOf(wateringInterval?.toLong(), wateredAt)
            )
            if (sprayingInterval != null) {
                schedule[ScheduleType.SPRAYING] = listOf(sprayingInterval.toLong(), sprayedAt)
            }
            if (fertilizingInterval != null) {
                schedule[ScheduleType.FERTILIZING] = listOf(
                    fertilizingInterval.toLong(), fertilizedAt
                )
            }
            if (rotatingInterval != null) {
                schedule[ScheduleType.ROTATING] = listOf(rotatingInterval.toLong(), rotatedAt)
            }
            return schedule
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

        restoreCareScheduleItem(schedule, dialogBinding, scheduleType)
        setDialogListeners(dialog, dialogBinding, scheduleType)

        dialog.show()
    }

    private fun setDialogListeners(
        dialog: BottomSheetDialog,
        dialogBinding: BottomSheetBinding,
        scheduleType: ScheduleType,
    ) {
        dialogBinding.okButton.setOnClickListener {
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
            careScheduleItem.binding.itemSwitch.isChecked = true
            updateCareSchedule(
                dialogBinding,
                careScheduleItem
            )
            dialog.dismiss()
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.cancel()
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
            (binding.itemSwitch as View).setOnTouchListener { switchView, event ->
                when {
                    event.action == MotionEvent.ACTION_UP && !binding.itemSwitch.isChecked -> {
                        onScheduleItemClickListener(careScheduleItem)
                        switchView.performClick()
                    }

                    event.action == MotionEvent.ACTION_UP && binding.itemSwitch.isChecked -> {
                        viewModel.clearScheduleField(state.scheduleItemType)
                    }
                }
                event.actionMasked == MotionEvent.ACTION_MOVE
            }
        }
    }

    private fun restoreCareScheduleItem(
        regularSchedule: RegularSchedule,
        dialogBinding: BottomSheetBinding,
        careScheduleItem: ScheduleType,
    ) {
        var defaultIntervalValue = 0
        var lastCareIntervalValue = 0

        when (careScheduleItem) {
            ScheduleType.WATERING -> binding.wateringListItem.apply {
                defaultIntervalValue = regularSchedule.wateringInterval ?: 0
                lastCareIntervalValue = getDaysFromTimestampAgo(regularSchedule.wateredAt)
            }

            ScheduleType.SPRAYING -> binding.sprayingListItem.apply {
                defaultIntervalValue = regularSchedule.sprayingInterval ?: 0
                lastCareIntervalValue = getDaysFromTimestampAgo(regularSchedule.sprayedAt)
            }

            ScheduleType.FERTILIZING -> binding.fertilizingListItem.apply {
                defaultIntervalValue = regularSchedule.fertilizingInterval ?: 0
                lastCareIntervalValue = getDaysFromTimestampAgo(regularSchedule.fertilizedAt)
            }

            ScheduleType.ROTATING -> binding.rotatingListItem.apply {
                defaultIntervalValue = regularSchedule.rotatingInterval ?: 0
                lastCareIntervalValue = getDaysFromTimestampAgo(regularSchedule.rotatedAt)
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

        if (defaultIntervalValue <= 0) {
            careScheduleItem.binding.itemSwitch.isChecked = false
            return
        }

        val lastCareValue = dialogBinding.lastCareItem.getSliderValue().toInt()

        careScheduleItem.setItemDescription(
            defaultIntervalValue,
            lastCareValue
        )
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

