package com.anonlatte.florarium.app.utils.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.net.toUri
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
     * It also compresses image before saving.
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

            file.toUri().also { compressImage(context, it) }
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

    /** Overwrites image file with compressed image. */
    fun compressImage(context: Context, imageUri: Uri) {
        kotlin.runCatching {
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                context.contentResolver.openOutputStream(imageUri).use { outputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmap.compress(
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

    private fun getRequiredWebpConfig(): Bitmap.CompressFormat {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSLESS
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }
    }

    companion object {

        private const val COMPRESSION_QUALITY = 85
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