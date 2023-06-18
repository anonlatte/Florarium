package com.anonlatte.florarium.app.utils.photo

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.anonlatte.florarium.app.utils.photo.PhotoPicker.PhotoPickListener

/**
 * This class is used to pick photo from gallery.
 * It uses [ActivityResultContracts.PickVisualMedia] to pick photo.
 * And [ImageFilesStorageManager] to save it to storage.
 * @param fragment fragment that will be used to register for activity result
 * @param photoPickListener listener that will be called when photo is picked
 * @see [ActivityResultContracts.PickVisualMedia]
 * @see [PhotoPickListener]
 * @see [ImageFilesStorageManager]
 */
class PhotoPicker(
    fragment: Fragment,
    photoPickListener: PhotoPickListener,
) : PhotoHandler(fragment) {

    private val pickPhotoAction = fragment.registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            localImageFilePath = storageManager.saveImageFile(context, uri)
            launchImageCompressionJob {
                photoPickListener.onPicked(localImageFilePath)
            }
        }
    }

    /**
     * This method is used to pick photo from gallery.
     */
    fun pickPhoto() {
        pickPhotoAction.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    /**
     * This interface is used to listen for photo pick event.
     */
    fun interface PhotoPickListener {

        /**
         * This method is called when photo is picked.
         */
        fun onPicked(uri: Uri?)
    }
}