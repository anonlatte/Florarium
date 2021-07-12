package com.anonlatte.florarium.extensions

import android.graphics.drawable.Drawable
import android.widget.TextView

fun TextView.setIcon(
    left: Drawable? = null,
    top: Drawable? = null,
    right: Drawable? = null,
    bottom: Drawable? = null
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}