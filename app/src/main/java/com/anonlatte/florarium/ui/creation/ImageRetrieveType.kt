package com.anonlatte.florarium.ui.creation

import android.content.Context
import androidx.annotation.StringRes
import com.anonlatte.florarium.R

/** Contains image retrieve types */
enum class ImageRetrieveType(@StringRes private val retrieveWayTextRes: Int) {
    CAMERA(R.string.dialog_get_image_from_camera),
    GALLERY(R.string.dialog_get_image_from_gallery);

    companion object {

        /**
         * Returns array of [ImageRetrieveType] text.
         * For example: ["Camera", "Gallery"]
         */
        fun getRetrieveWaysText(context: Context): Array<String> {
            return values().map { context.getString(it.retrieveWayTextRes) }.toTypedArray()
        }
    }
}
