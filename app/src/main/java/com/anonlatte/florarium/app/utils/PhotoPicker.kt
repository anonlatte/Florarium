package com.anonlatte.florarium.app.utils

import android.net.Uri
import android.os.Build
import android.os.ext.SdkExtensions
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class PhotoPicker(fragment: Fragment, private val photoPickListener: PhotoPickListener) {

    private val pickPhotoActionBeforeR = fragment.registerForActivityResult(
        ActivityResultContracts.GetContent(),
        photoPickListener::onPicked
    )

    private val pickPhotoAction = fragment.registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
        photoPickListener::onPicked
    )

    fun pickPhoto() {
        if (isPhotoPickerAvailable()) {
            pickPhotoAction.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            pickPhotoActionBeforeR.launch("image/*")
        }
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2
        } else {
            false
        }
    }

    fun interface PhotoPickListener {
        fun onPicked(uri: Uri?)
    }
}