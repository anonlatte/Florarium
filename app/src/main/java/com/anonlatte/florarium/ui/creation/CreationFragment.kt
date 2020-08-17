package com.anonlatte.florarium.ui.creation

import android.app.Activity.RESULT_OK
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.anonlatte.florarium.R
import com.anonlatte.florarium.alarms.PlantsNotificationReceiver
import com.anonlatte.florarium.databinding.BottomSheetBinding
import com.anonlatte.florarium.databinding.FragmentPlantCreationBinding
import com.anonlatte.florarium.databinding.ListItemScheduleBinding
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.db.models.ScheduleType
import com.anonlatte.florarium.utilities.PROVIDER_AUTHORITY
import com.anonlatte.florarium.utilities.REQUEST_IMAGE_CAPTURE
import com.anonlatte.florarium.utilities.REQUEST_IMAGE_SELECT
import com.anonlatte.florarium.utilities.TimeStampHelper.getDaysFromTimestampAgo
import com.anonlatte.florarium.utilities.TimeStampHelper.getTimestampFromDaysAgo
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class CreationFragment : Fragment() {
    private val viewModel by viewModels<CreationViewModel>()
    private var _binding: FragmentPlantCreationBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentPhotoPath: String
    private val imageFile: File by lazy {
        createImageFile()
    }
    private var isPlantCreated = false
    private val passedPlant: Plant? by lazy {
        arguments?.getParcelable<Plant>("plant")
    }
    private val passedSchedule: RegularSchedule? by lazy {
        arguments?.getParcelable<RegularSchedule>("schedule")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantCreationBinding.inflate(inflater, container, false)
        _binding!!.viewModel = viewModel

        if (passedPlant != null) {
            viewModel.plant = passedPlant!!
            viewModel.isPlantExist = true
        }

        if (passedSchedule != null) {
            binding.viewModel!!.regularSchedule = passedSchedule!!
        }

        setListeners()
        makeScheduleItemsClickable()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (!isPlantCreated) {
            imageFile.delete()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_SELECT -> {
                    resultData?.data?.also { uri ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            val inputStream = requireContext().contentResolver.openInputStream(uri)
                            val outputStream = FileOutputStream(imageFile)
                            var bitmap: Bitmap? = null
                            if (inputStream != null) {
                                bitmap = BitmapFactory.decodeStream(inputStream)
                                inputStream.close()
                            }
                            bitmap?.compress(Bitmap.CompressFormat.WEBP, 85, outputStream)
                            outputStream.flush()
                            outputStream.close()
                            viewModel.plant.imageUrl = currentPhotoPath
                            Timber.d("File ${imageFile.name} is created in $currentPhotoPath")
                        }
                        Glide.with(requireContext())
                            .load(uri)
                            .into(binding.plantImageView)
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    Glide.with(requireContext())
                        .load(currentPhotoPath)
                        .into(binding.plantImageView)
                    viewModel.plant.imageUrl = currentPhotoPath
                }
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
                // TODO visualize waiting without locking main thread
                createAlarms()
                isPlantCreated = true
                Toast.makeText(
                    requireContext(),
                    getString(R.string.message_plant_is_added),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
        binding.titleEditText.doOnTextChanged { text, _, _, _ ->
            when {
                text.isNullOrEmpty() -> {
                    binding.titleInputLayout.error = getString(R.string.error_empty_plant_name)
                }
                text.length > 40 -> {
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

    private fun createAlarms() {
        val scheduleMap = getScheduleMap()
        val alarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        scheduleMap.keys.forEach {
            setAlarm(
                alarmManager,
                it.name.toLowerCase(Locale.ROOT),
                scheduleMap[it]!![0]!!.toLong(),
                scheduleMap[it]!![1]
            )
        }
    }

    private fun setAlarm(
        alarmManager: AlarmManager?,
        eventTag: String,
        interval: Long,
        lastCare: Long?
    ) = runBlocking {
        // Delay to set an unique id from `System.currentTimeMillis()`
        delay(1000)
        val randomRequestId = (System.currentTimeMillis() / 1000).toInt()
        val formattedInterval = interval * AlarmManager.INTERVAL_DAY
        val careLeftDays = if (lastCare != null) {
            val value =
                formattedInterval - getDaysFromTimestampAgo(lastCare) * AlarmManager.INTERVAL_DAY
            if (value < 1) {
                System.currentTimeMillis()
            } else {
                value
            }
        } else {
            System.currentTimeMillis() + formattedInterval
        }
        val plantsAlarmIntent =
            Intent(requireContext(), PlantsNotificationReceiver::class.java).apply {
                action = "PLANT_EVENT"
                putExtra("event", eventTag)
                putExtra("plant-name", viewModel.plant.name)
            }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            randomRequestId,
            plantsAlarmIntent,
            0
        )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
        alarmManager?.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            careLeftDays,
            formattedInterval,
            pendingIntent
        )
        Timber.tag("alarm")
            .d("${viewModel.plant.name} in $interval - $eventTag with id:$randomRequestId")
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
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).also { selectPictureIntent ->
            startActivityForResult(selectPictureIntent, REQUEST_IMAGE_SELECT)
        }
    }

    private fun openImageCaptureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile = try {
                    createImageFile()
                } catch (exception: IOException) {
                    Timber.e(exception)
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        PROVIDER_AUTHORITY,
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun getFormattedTimeStamp(): String =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = getFormattedTimeStamp()
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
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
            /**
             * TODO check if DefaultCareValue slider < 0 then don't execute
             *  move out checking condition from [updateCareSchedule]
             */
            itemScheduleBinding.itemSwitch.isChecked = true
            updateCareSchedule(
                dialogBinding,
                itemScheduleBinding
            )
            dialog.dismiss()
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun addSliderListeners(dialogBinding: BottomSheetBinding) {
        with(dialogBinding) {
            defaultIntervalItem.daySlider.addOnChangeListener { _, value, _ ->
                defaultIntervalItem.title =
                    getString(R.string.title_interval_in_days, value.toInt())
            }
            winterIntervalItem.daySlider.addOnChangeListener { _, value, _ ->
                winterIntervalItem.title =
                    getString(R.string.title_interval_for_winter, value.toInt())
            }
            lastCareItem.daySlider.addOnChangeListener { _, value, _ ->
                lastCareItem.title = getString(R.string.title_last_care, value.toInt())
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
        itemScheduleBinding: ListItemScheduleBinding
    ) {
        val defaultIntervalValue = dialogBinding.defaultIntervalItem.daySlider.value.toInt()

        if (defaultIntervalValue <= 0) {
            itemScheduleBinding.itemSwitch.isChecked = false
            return
        }

        val winterIntervalValue = dialogBinding.winterIntervalItem.daySlider.value.toInt()
        val lastCareValue = dialogBinding.lastCareItem.daySlider.value.toInt()

        itemScheduleBinding.scheduleValue = formattedScheduleValue(
            defaultIntervalValue,
            winterIntervalValue,
            lastCareValue
        )

        with(viewModel) {
            when (ScheduleType.toScheduleType(itemScheduleBinding.scheduleItemType)) {
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
        defaultIntervalValue: Int,
        winterIntervalValue: Int,
        lastCareValue: Int
    ): String = if (lastCareValue > 0 && winterIntervalValue > 0) {
        "$lastCareValue $defaultIntervalValue/$winterIntervalValue"
    } else if (lastCareValue <= 0 && winterIntervalValue > 0) {
        "$defaultIntervalValue/$winterIntervalValue"
    } else if (lastCareValue > 0 && winterIntervalValue <= 0) {
        "$lastCareValue $defaultIntervalValue"
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
