package com.anonlatte.florarium.ui.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.res.getStringOrThrow
import com.anonlatte.florarium.R
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.list_item_bottom_sheet.view.*

class BottomSheetItem(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomSheetItem)
    val title: String?
    val icon: Drawable?

    init {
        inflate(context, R.layout.list_item_bottom_sheet, this)
        title = typedArray.getStringOrThrow(R.styleable.BottomSheetItem_title)
        icon = typedArray.getDrawable(R.styleable.BottomSheetItem_icon)
        intervalTitle.text = title
        intervalIcon.setImageDrawable(icon)
        intervalSlider.addOnChangeListener { _, value, _ ->
            intervalTitle.text = when (this) {
                defaultIntervalItem -> context.getString(
                    R.string.title_interval_in_days,
                    value.toInt()
                )
                winterIntervalItem -> context.getString(
                    R.string.title_interval_for_winter,
                    value.toInt()
                )
                lastCareItem -> context.getString(R.string.title_last_care, value.toInt())
                else -> null
            }
        }
        typedArray.recycle()
    }
}
