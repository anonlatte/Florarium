package com.anonlatte.florarium.app.utils.photo

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

/**
 * Prevents memory leaks by clearing context when fragment is destroyed.
 */
open class PhotoHandler(fragment: Fragment) {

    private var _context: Context? = null
    protected val context: Context get() = _context!!

    /** Path to the created image file. */
    protected var localImageFilePath: Uri? = null

    protected val storageManager = ImageFilesStorageManager()

    private val lifecycleCoroutineScope = fragment.lifecycleScope

    private val _imageCompressionFlow = MutableStateFlow(false)

    /** Emits true when compression is in progress and false when it's finished. */
    val imageCompressionFlow: StateFlow<Boolean> = _imageCompressionFlow.asStateFlow()

    init {
        fragment.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    _context = fragment.requireContext()
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    _context = null
                }
            }
        )
    }

    // TODO: should be called each time when create file to prevent a lot of unused files
    /** Deletes created image file. */
    fun clearCreatedImageFiles() {
        if (File(localImageFilePath?.path ?: return).delete()) {
            Timber.d("Deleted file: ${localImageFilePath?.path}")
        } else {
            Timber.d("Failed to delete file: ${localImageFilePath?.path}")
        }
    }

    /**
     * Launches coroutine that compresses image.
     * Emits true when compression is in progress and false when it's finished.
     */
    protected fun launchImageCompressionJob(onCompressed: () -> Unit) {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            localImageFilePath?.let {
                _imageCompressionFlow.emit(true)
                storageManager.compressImage(context, it)
                _imageCompressionFlow.emit(false)
            }
            onCompressed()
        }
    }
}
