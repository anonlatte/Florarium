package com.anonlatte.florarium.ui.creation

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.anonlatte.florarium.R
import com.anonlatte.florarium.alarms.PlantsNotificationReceiver
import com.anonlatte.florarium.databinding.BottomSheetBinding
import com.anonlatte.florarium.databinding.FragmentPlantCreationBinding
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.PlantAlarm
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.db.models.ScheduleType
import com.anonlatte.florarium.extensions.setIcon
import com.anonlatte.florarium.ui.custom.CareScheduleItem
import com.anonlatte.florarium.utilities.PLANT_NOTIFICATION_EVENT
import com.anonlatte.florarium.utilities.PROVIDER_AUTHORITY
import com.anonlatte.florarium.utilities.TimeStampHelper.getDaysFromTimestampAgo
import com.anonlatte.florarium.utilities.TimeStampHelper.getTimestampFromDaysAgo
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.TestOnly
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// TODO: 01-Nov-20 sometimes plant image doesn't appear on release build
class CreationFragment : Fragment() {
    private val viewModel by viewModels<CreationViewModel>()
    private var _binding: FragmentPlantCreationBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentPhotoPath: String
    private val imageFile: File by lazy {
        createImageFile()
    }
    private val passedPlant: Plant? by lazy {
        arguments?.getParcelable("plant")
    }
    private val passedSchedule: RegularSchedule? by lazy {
        arguments?.getParcelable("schedule")
    }

    private val imageSelectAction = registerForActivityResult(GetContent()) { uri ->
        lifecycleScope.launch {
            storeBitmapFromUri(uri)
            viewModel.plant.imageUrl = uri.path
            Timber.d("File ${imageFile.name} is created in ${uri.path}")
        }
        Glide.with(requireContext())
            .load(uri)
            .into(binding.plantImageView)
    }
    private val takePictureAction = registerForActivityResult(TakePicture()) { imageTaken ->
        if (imageTaken) {
            Glide.with(requireContext())
                .load(currentPhotoPath)
                .into(binding.plantImageView)
            viewModel.plant.imageUrl = currentPhotoPath
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantCreationBinding.inflate(inflater, container, false)

        if (passedPlant != null) {
            viewModel.plant = passedPlant!!
            viewModel.isPlantExist = true
        }

        if (passedSchedule != null) {
            // TODO if field is null then hide/show 'not set'
            viewModel.regularSchedule = passedSchedule!!
            restoreCareSchedule()
        }

        subscribeUi()
        setListeners()
        makeScheduleItemsClickable()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.plant.name?.let { plantName ->
            if (plantName.isNotEmpty()) {
                binding.titleEditText.setText(plantName)
            }
        }
    }

    private fun subscribeUi() {
        viewModel.isPlantCreated.observe(
            viewLifecycleOwner,
            {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.message_plant_is_added),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
            }
        )
    }

    private fun restoreCareSchedule() {
        binding.wateringListItem.setItemDescription(
            formattedScheduleValue(
                viewModel.regularSchedule.wateringInterval,
                viewModel.winterSchedule.wateringInterval,
                getDaysFromTimestampAgo(viewModel.regularSchedule.wateredAt)
            )
        )
        binding.sprayingListItem.setItemDescription(
            formattedScheduleValue(
                viewModel.regularSchedule.sprayingInterval,
                viewModel.winterSchedule.sprayingInterval,
                getDaysFromTimestampAgo(viewModel.regularSchedule.sprayedAt)
            )
        )
        binding.fertilizingListItem.setItemDescription(
            formattedScheduleValue(
                viewModel.regularSchedule.fertilizingInterval,
                viewModel.winterSchedule.fertilizingInterval,
                getDaysFromTimestampAgo(viewModel.regularSchedule.fertilizedAt)
            )
        )
        binding.rotatingListItem.setItemDescription(
            formattedScheduleValue(
                viewModel.regularSchedule.rotatingInterval,
                viewModel.winterSchedule.rotatingInterval,
                getDaysFromTimestampAgo(viewModel.regularSchedule.rotatedAt)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (!viewModel.isPlantCreated.value!!) {
            imageFile.delete()
        }
    }

    private fun storeBitmapFromUri(uri: Uri): Bitmap? {
        return requireContext().contentResolver.openInputStream(uri)?.let { inputStream ->
            val outputStream = FileOutputStream(imageFile)
            BitmapFactory.decodeStream(inputStream).also { bitmap ->
                inputStream.close()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 85, outputStream)
                } else {
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 85, outputStream)
                }
                outputStream.flush()
                outputStream.close()
            }
        }
    }

    // TODO add extended validation
    private fun setListeners() {
        binding.addPlantButton.setOnClickListener {
            if (binding.titleEditText.text.isNullOrEmpty()) {
                binding.titleInputLayout.error = getString(R.string.error_empty_plant_name)
            } else if (binding.titleInputLayout.error == null) {
                binding.titleInputLayout.error = null
                viewModel.addPlantToGarden()
                binding.progressCreation.isVisible = true
                lifecycleScope.launch {
                    createAlarms()
                    viewModel.isPlantCreated.postValue(true)
                }
            }
        }
        binding.titleEditText.doOnTextChanged { text, _, _, _ ->
            when {
                text.isNullOrEmpty() -> {
                    binding.titleInputLayout.error = getString(R.string.error_empty_plant_name)
                }
                text.length > binding.titleInputLayout.counterMaxLength -> {
                    binding.titleInputLayout.error =
                        getString(
                            R.string.error_long_plant_name,
                            text.length
                        )
                }
                else -> {
                    binding.titleInputLayout.error = null
                }
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

    private suspend fun createAlarms() = withContext(Dispatchers.IO) {
        val scheduleMap = getScheduleMap()
        val alarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        scheduleMap.keys.forEach { scheduleType ->
            val randomRequestId = System.currentTimeMillis() / 1000
            val plantAlarm = PlantAlarm(
                randomRequestId,
                viewModel.plant.name ?: UUID.randomUUID().toString(),
                scheduleType.name.lowercase(),
                scheduleMap[scheduleType]!![0]!!.toLong(), // interval
                scheduleMap[scheduleType]!![1] // last care
            ).also { plantAlarm ->
                val plantsAlarmIntent =
                    Intent(context, PlantsNotificationReceiver::class.java).apply {
                        action = PLANT_NOTIFICATION_EVENT
                        putExtra("alarm", plantAlarm)
                    }
                plantAlarm.setAlarm(
                    requireContext(),
                    plantsAlarmIntent,
                    alarmManager
                )
            }
            viewModel.addPlantAlarm(plantAlarm)
            delay(1000)
        }
    }

    private fun getScheduleMap(): MutableMap<ScheduleType, List<Long?>> {
        with(viewModel.regularSchedule) {
            val schedule =
                mutableMapOf(ScheduleType.WATERING to listOf(wateringInterval?.toLong(), wateredAt))
            if (sprayingInterval != null) {
                schedule[ScheduleType.SPRAYING] = listOf(sprayingInterval?.toLong(), sprayedAt)
            }
            if (fertilizingInterval != null) {
                schedule[ScheduleType.FERTILIZING] =
                    listOf(fertilizingInterval?.toLong(), fertilizedAt)
            }
            if (rotatingInterval != null) {
                schedule[ScheduleType.ROTATING] = listOf(rotatingInterval?.toLong(), rotatedAt)
            }
            return schedule
        }
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
        imageSelectAction.launch("image/*")
    }

    private fun openImageCaptureIntent() {
        runCatching {
            createImageFile()
        }.onFailure {
            Timber.e(it)
        }.getOrNull()?.also {
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                PROVIDER_AUTHORITY,
                it
            )
            takePictureAction.launch(photoUri)
        }
    }

    private fun getFormattedTimeStamp(): String =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    private fun createImageFile(): File {
        val timeStamp = getFormattedTimeStamp()
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun onScheduleItemClickListener(
        careScheduleItem: CareScheduleItem,
        title: String?,
        icon: Drawable?
    ) {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = BottomSheetBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)

        dialogBinding.bottomSheetTitle.text = title
        dialogBinding.bottomSheetTitle.setIcon(left = icon)

        restoreCareScheduleItem(dialogBinding, careScheduleItem)
        setDialogListeners(dialog, dialogBinding, careScheduleItem)

        dialog.show()
    }

    private fun setDialogListeners(
        dialog: BottomSheetDialog,
        dialogBinding: BottomSheetBinding,
        careScheduleItem: CareScheduleItem
    ) {
        dialogBinding.okButton.setOnClickListener {
            /**
             * TODO check if DefaultCareValue slider < 0 then don't execute
             *  move out checking condition from [updateCareSchedule]
             */
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

    private fun setScheduleItemListener(careScheduleItem: CareScheduleItem) {
        with(careScheduleItem) {
            setOnClickListener {
                onScheduleItemClickListener(
                    careScheduleItem,
                    title,
                    icon
                )
            }
            /** Convert itemSwitch to View to avoid overriding [View.performClick] */
            (binding.itemSwitch as View).setOnTouchListener { switchView, event ->
                if (event.action == MotionEvent.ACTION_UP && !binding.itemSwitch.isChecked) {
                    onScheduleItemClickListener(careScheduleItem, title, icon)
                    switchView.performClick()
                } else if (event.action == MotionEvent.ACTION_UP && binding.itemSwitch.isChecked) {
                    clearScheduleFields(careScheduleItem.scheduleItemType)
                }
                event.actionMasked == MotionEvent.ACTION_MOVE
            }
        }
    }

    private fun restoreCareScheduleItem(
        dialogBinding: BottomSheetBinding,
        careScheduleItem: CareScheduleItem
    ) {
        var defaultIntervalValue = 0
        var winterIntervalValue = 0
        var lastCareIntervalValue = 0
        with(viewModel) {
            when (careScheduleItem) {
                binding.wateringListItem -> {
                    defaultIntervalValue = regularSchedule.wateringInterval ?: 0
                    winterIntervalValue = winterSchedule.wateringInterval ?: 0
                    lastCareIntervalValue =
                        getDaysFromTimestampAgo(regularSchedule.wateredAt)
                }
                binding.sprayingListItem -> {
                    defaultIntervalValue = regularSchedule.sprayingInterval ?: 0
                    winterIntervalValue = winterSchedule.sprayingInterval ?: 0
                    lastCareIntervalValue =
                        getDaysFromTimestampAgo(regularSchedule.sprayedAt)
                }
                binding.fertilizingListItem -> {
                    defaultIntervalValue = regularSchedule.fertilizingInterval ?: 0
                    winterIntervalValue = winterSchedule.fertilizingInterval ?: 0
                    lastCareIntervalValue =
                        getDaysFromTimestampAgo(regularSchedule.fertilizedAt)
                }
                binding.rotatingListItem -> {
                    defaultIntervalValue = regularSchedule.rotatingInterval ?: 0
                    winterIntervalValue = winterSchedule.rotatingInterval ?: 0
                    lastCareIntervalValue =
                        getDaysFromTimestampAgo(regularSchedule.rotatedAt)
                }
            }
        }
        dialogBinding.defaultIntervalItem.setTitle(
            getString(R.string.title_interval_in_days, defaultIntervalValue)
        )
        dialogBinding.defaultIntervalItem.setSliderValue(defaultIntervalValue.toFloat())

        dialogBinding.winterIntervalItem.setTitle(
            getString(R.string.title_interval_for_winter, winterIntervalValue)
        )
        dialogBinding.winterIntervalItem.setSliderValue(winterIntervalValue.toFloat())

        dialogBinding.lastCareItem.setTitle(
            getString(R.string.title_last_care, lastCareIntervalValue)
        )
        dialogBinding.lastCareItem.setSliderValue(lastCareIntervalValue.toFloat())
    }

    private fun updateCareSchedule(
        dialogBinding: BottomSheetBinding,
        careScheduleItem: CareScheduleItem
    ) {
        val defaultIntervalValue = dialogBinding.defaultIntervalItem.getSliderValue().toInt()

        if (defaultIntervalValue <= 0) {
            careScheduleItem.binding.itemSwitch.isChecked = false
            return
        }

        val winterIntervalValue = dialogBinding.winterIntervalItem.getSliderValue().toInt()
        val lastCareValue = dialogBinding.lastCareItem.getSliderValue().toInt()

        careScheduleItem.setItemDescription(
            formattedScheduleValue(
                defaultIntervalValue,
                winterIntervalValue,
                lastCareValue
            )
        )

        with(viewModel) {
            when (ScheduleType.toScheduleType(careScheduleItem.scheduleItemType)) {
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

    private fun formattedScheduleValue(
        defaultIntervalValue: Int?,
        winterIntervalValue: Int?,
        lastCareValue: Int?
    ): String =
        if (lastCareValue != null && lastCareValue > 0) {
            if (winterIntervalValue != null && winterIntervalValue > 0) {
                "$lastCareValue $defaultIntervalValue/$winterIntervalValue"
            } else {
                "$lastCareValue $defaultIntervalValue"
            }
        } else if (winterIntervalValue != null && winterIntervalValue > 0) {
            "$defaultIntervalValue/$winterIntervalValue"
        } else {
            defaultIntervalValue.toString()
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
            binding.wateringListItem,
            getString(R.string.tooltip_watering)
        )
        TooltipCompat.setTooltipText(
            binding.sprayingListItem,
            getString(R.string.tooltip_spraying)
        )
        TooltipCompat.setTooltipText(
            binding.fertilizingListItem,
            getString(R.string.tooltip_fertilizing)
        )
        TooltipCompat.setTooltipText(
            binding.rotatingListItem,
            getString(R.string.tooltip_rotating)
        )
    }

    @TestOnly
    fun getTitleInputLayoutMaxLength(): Int = binding.titleInputLayout.counterMaxLength
}

