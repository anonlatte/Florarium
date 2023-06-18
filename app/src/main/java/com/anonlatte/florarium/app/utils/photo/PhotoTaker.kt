package com.anonlatte.florarium.app.utils.photo

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.anonlatte.florarium.app.utils.photo.ImageFilesStorageManager.Companion.toUri
import com.anonlatte.florarium.app.utils.photo.PhotoTaker.PhotoTakerListener

/**
 * Class for taking photos from camera.
 * @param fragment fragment that will be used for taking photos.
 * @param pickListener listener that will be called when photo is taken.
 * @see PhotoTakerListener
 */
class PhotoTaker(
    fragment: Fragment,
    pickListener: PhotoTakerListener,
) : PhotoHandler(fragment) {

    private val photoTakeAction = fragment.registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { imageTaken ->
        if (imageTaken) {
            val compressedImage = localImageFilePath?.also {
                storageManager.compressImage(context, it)
            }
            pickListener.onPhotoTake(compressedImage)
        }
    }

    /**
     * Starts taking photo from camera
     */
    fun takePhoto() {
        localImageFilePath = storageManager.createImageFile(context).toUri(context)
        photoTakeAction.launch(localImageFilePath)
    }

    /**
     * Listener for taking photos.
     * @see takePhoto
     */
    fun interface PhotoTakerListener {

        /**
         * Called when photo is taken.
         * @param uri path to taken photo.
         */
        fun onPhotoTake(uri: Uri?)
    }

}
