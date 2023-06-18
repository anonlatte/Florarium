package com.anonlatte.florarium.app.utils.photo

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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

    /** Deletes created image file. */
    fun clearCreatedImageFiles() {
        File(localImageFilePath?.path ?: return).delete()
        Timber.d("Deleted file: ${localImageFilePath?.path}")
    }
}
