package com.anonlatte.florarium.app.utils.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.anonlatte.florarium.app.utils.PROVIDER_AUTHORITY
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * This class is used to save image files to application storage.
 * @see [ImageFilesStorageManager.saveImageFile]
 * @see [ImageFilesStorageManager.createImageFile]
 */
class ImageFilesStorageManager {

    private val timeStamp
        get() = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    /**
     * This method is used to save file to application storage.
     * @param context context that will be used to get application storage directory
     * @param imageUri uri of the image that will be saved
     */
    fun saveImageFile(context: Context, imageUri: Uri): Uri? {
        val file = createImageFile(context)
        return kotlin.runCatching {
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            file.toUri()
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * Creates image file in application storage.
     * File name format: "yyyyMMdd_HHmmss.webp"
     */
    fun createImageFile(context: Context): File {
        val fileName = "${timeStamp}.${IMAGE_EXTENSION}"

        val imageDir = context.filesDir
        val imageFile = File(imageDir, fileName)

        // Create the parent directories if they don't exist
        imageFile.parentFile?.mkdirs()

        // Create the image file
        imageFile.createNewFile()
        return imageFile
    }

    /**
     * Overwrites image file with compressed image.
     */
    fun compressImage(context: Context, imageUri: Uri) {
        kotlin.runCatching {
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val imageRotationAngle = getRotationFromExif(context, imageUri)
                context.contentResolver.openOutputStream(imageUri).use { outputStream ->
                    bitmap?.rotate(
                        imageRotationAngle
                    )?.compress(
                        getRequiredWebpConfig(),
                        COMPRESSION_QUALITY,
                        outputStream
                    )
                }
            }

        }.onFailure {
            Timber.e(it)
        }
    }

    /**
     * Returns required webp config for current android version.
     * @see [Bitmap.CompressFormat.WEBP]
     * @see [Bitmap.CompressFormat.WEBP_LOSSY]
     */
    private fun getRequiredWebpConfig(): Bitmap.CompressFormat {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }
    }

    /**
     * Returns image rotation angle from exif data.
     * @param context context that will be used to get image input stream
     * @param imageUri uri of the image that will be used to get exif data
     * @return rotation angle in degrees
     */
    private fun getRotationFromExif(context: Context, imageUri: Uri): Int {
        return kotlin.runCatching {

            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return 0
            val exifInterface = ExifInterface(inputStream)

            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val rotation: Int = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }

            rotation
        }.getOrDefault(0)
    }

    /**
     * Rotates bitmap by given angle.
     * @param degrees angle in degrees
     * @return rotated bitmap
     */
    private fun Bitmap.rotate(degrees: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    companion object {

        private const val COMPRESSION_QUALITY = 0
        private const val IMAGE_EXTENSION = "webp"

        /** Returns uri of the file if it exists in application storage. */
        fun File.toUri(context: Context): Uri? {
            return if (exists()) {
                FileProvider.getUriForFile(context, PROVIDER_AUTHORITY, this)
            } else {
                null
            }
        }
    }
}