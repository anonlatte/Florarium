package com.anonlatte.florarium.ui.creation

import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.anonlatte.florarium.R
import com.anonlatte.florarium.app.utils.PROVIDER_AUTHORITY
import com.anonlatte.florarium.app.utils.getDaysFromTimestampAgo
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.data.model.ScheduleType
import com.anonlatte.florarium.databinding.BottomSheetBinding
import com.anonlatte.florarium.databinding.FragmentPlantCreationBinding
import com.anonlatte.florarium.extensions.appComponent
import com.anonlatte.florarium.extensions.launchWhenStarted
import com.anonlatte.florarium.extensions.setAlarm
import com.anonlatte.florarium.extensions.setIcon
import com.anonlatte.florarium.ui.MainActivity
import com.anonlatte.florarium.ui.custom.CareScheduleItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.onEach
import org.jetbrains.annotations.TestOnly
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

// TODO: 01-Nov-20 sometimes plant image doesn't appear on release build
class CreationFragment : Fragment() {
    private var _binding: FragmentPlantCreationBinding? = null
    private val binding get() = _binding!!

    private val navArgs: CreationFragmentArgs by navArgs()

    private val imageFile: File by lazy { createImageFile() }
    private val currentPhotoPath: String get() = imageFile.absolutePath

    private val imageSelectAction = registerForActivityResult(GetContent()) { uri ->
        if (uri == null) return@registerForActivityResult
        if (!isImageSaved()) return@registerForActivityResult
        saveDataFromUri(imageFile, uri)
        Timber.d("File ${imageFile.name} is created in ${uri.path}")
        viewModel.updatePlantImage(currentPhotoPath)
        binding.plantImageView.load(uri)
    }

    private val takePictureAction = registerForActivityResult(TakePicture()) { imageTaken ->
        if (imageTaken) {
            if (!isImageSaved()) return@registerForActivityResult
            Timber.d("File ${imageFile.name} is created in $currentPhotoPath")
            viewModel.updatePlantImage(currentPhotoPath)
            binding.plantImageView.load(File(currentPhotoPath))
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
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantCreationBinding.inflate(inflater, container, false)
        navArgs.plant?.let { plant ->
            viewModel.setPlant(plant)
            viewModel.updatePlantExistence(true)
            navArgs.schedule?.let { schedule ->
                // TODO if field is null then hide/show 'not set'
                viewModel.setSchedule(schedule)
                restoreCareSchedule()
            }
            (requireActivity() as MainActivity).supportActionBar?.title = getString(
                R.string.label_fragment_plant_update
            )
            binding.plantImageView.load("file://${plant.imageUrl}") {
                listener(
                    onError = { _, errorResult ->
                        Timber.e(errorResult.throwable)
                    }
                )
            }
        }
        subscribeUi()
        setListeners()
        addImageButtonTooltip()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (navArgs.plant != null) {
            binding.btnAddPlant.apply {
                setText(R.string.btn_save)
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_outline_save_24)
            }
        }
        if (viewModel.plant.name.isNotEmpty()) {
            binding.etTitle.setText(viewModel.plant.name)
        }
    }

    private fun isImageSaved(): Boolean {
        return if (!imageFile.exists()) {
            Toast.makeText(
                requireContext(),
                R.string.error_image_not_saved,
                Toast.LENGTH_SHORT
            ).show()
            false
        } else {
            true
        }
    }

    private fun subscribeUi() {
        viewModel.isPlantCreated.onEach {
            if (it) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.message_plant_is_added),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }.launchWhenStarted(lifecycleScope)
    }

    private fun restoreCareSchedule() {
        with(binding) {
            wateringListItem.setItemDescription(
                viewModel.regularSchedule.wateringInterval,
                getDaysFromTimestampAgo(viewModel.regularSchedule.wateredAt)
            )
            sprayingListItem.setItemDescription(
                viewModel.regularSchedule.sprayingInterval,
                getDaysFromTimestampAgo(viewModel.regularSchedule.sprayedAt)
            )
            fertilizingListItem.setItemDescription(
                viewModel.regularSchedule.fertilizingInterval,
                getDaysFromTimestampAgo(viewModel.regularSchedule.fertilizedAt)
            )
            rotatingListItem.setItemDescription(
                viewModel.regularSchedule.rotatingInterval,
                getDaysFromTimestampAgo(viewModel.regularSchedule.rotatedAt)

            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        imageFile.delete()
    }

    // TODO add extended validation
    private fun setListeners() {
        with(binding) {
            btnAddPlant.setOnClickListener {
                if (viewModel.plant.name.isEmpty()) {
                    tilTitle.error = getString(R.string.error_empty_plant_name)
                } else {
                    tilTitle.error = null
                    viewModel.addPlantToGarden()
                    progressCreation.isVisible = true
                    createAlarms()
                }
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
            setScheduleItemListener(wateringListItem)
            setScheduleItemListener(sprayingListItem)
            setScheduleItemListener(fertilizingListItem)
            setScheduleItemListener(rotatingListItem)
        }
    }

    private fun createAlarms() {
        val scheduleMap = getScheduleMap()

        scheduleMap.keys.forEach { scheduleType ->
            scheduleMap[scheduleType]?.get(0)?.let { interval ->
                val lastCare = scheduleMap[scheduleType]?.get(1)
                val randomRequestId = Random.nextLong()
                val plantAlarm = PlantAlarm(
                    requestId = randomRequestId,
                    plantName = viewModel.plant.name,
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

    private fun getScheduleMap(): MutableMap<ScheduleType, List<Long?>> {
        with(viewModel.regularSchedule) {
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
        }.show()
    }

    private fun openImageSelectIntent() {
        imageSelectAction.launch("image/*")
    }

    private fun openImageCaptureIntent() {
        val photoUri: Uri = FileProvider.getUriForFile(
            requireContext(),
            PROVIDER_AUTHORITY,
            imageFile
        )
        takePictureAction.launch(photoUri)
    }

    /** Returns string in format `yyyyMMdd_HHmmss`of current timestamp from [Date] instance */
    private fun getFormattedTimeStamp(): String = SimpleDateFormat(
        "yyyyMMdd_HHmmss", Locale.getDefault()
    ).format(Date())

    /**
     * Creates a file with name `yyyyMMdd_HHmmss.jpg`
     * */
    private fun createImageFile(): File {
        val timeStamp = getFormattedTimeStamp()
        val storageDir: File? = requireContext().getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    /**
     * Saves data from uri to file
     * */
    private fun saveDataFromUri(file: File, uri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return
        val outputStream = FileOutputStream(file)
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
        var lastCareIntervalValue = 0
        with(viewModel) {
            when (careScheduleItem) {
                binding.wateringListItem -> {
                    defaultIntervalValue = regularSchedule.wateringInterval ?: 0
                    lastCareIntervalValue = getDaysFromTimestampAgo(regularSchedule.wateredAt)
                }
                binding.sprayingListItem -> {
                    defaultIntervalValue = regularSchedule.sprayingInterval ?: 0
                    lastCareIntervalValue = getDaysFromTimestampAgo(regularSchedule.sprayedAt)
                }
                binding.fertilizingListItem -> {
                    defaultIntervalValue = regularSchedule.fertilizingInterval ?: 0
                    lastCareIntervalValue = getDaysFromTimestampAgo(regularSchedule.fertilizedAt)
                }
                binding.rotatingListItem -> {
                    defaultIntervalValue = regularSchedule.rotatingInterval ?: 0
                    lastCareIntervalValue = getDaysFromTimestampAgo(regularSchedule.rotatedAt)
                }
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
        careScheduleItem: CareScheduleItem
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
            scheduleItemType = ScheduleType.toScheduleType(careScheduleItem.scheduleItemType),
            defaultIntervalValue = defaultIntervalValue,
            lastCareValue = lastCareValue
        )
    }

    private fun clearScheduleFields(scheduleTypeValue: Int?) {
        viewModel.clearScheduleField(ScheduleType.toScheduleType(scheduleTypeValue))
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

