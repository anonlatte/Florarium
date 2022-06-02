package com.anonlatte.florarium.ui.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.core.content.res.getStringOrThrow
import com.anonlatte.florarium.R
import com.anonlatte.florarium.databinding.ListItemBottomSheetBinding

class BottomSheetItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomSheetItem)
    private val title: String?
    private val icon: Drawable?
    private val itemType: Int

    private val binding =
        ListItemBottomSheetBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        title = typedArray.getStringOrThrow(R.styleable.BottomSheetItem_title)
        icon = typedArray.getDrawable(R.styleable.BottomSheetItem_icon)
        itemType = typedArray.getInt(
            R.styleable.BottomSheetItem_item_type,
            BottomSheetItemType.DEFAULT.value
        )
        with(binding) {
            intervalTitle.text = title
            intervalIcon.setImageDrawable(icon)
            intervalSlider.addOnChangeListener { _, value, _ ->
                intervalTitle.text = when (itemType) {
                    BottomSheetItemType.DEFAULT.value -> context.getString(
                        R.string.title_interval_in_days,
                        value.toInt()
                    )
                    BottomSheetItemType.LAST_CARE.value -> context.getString(
                        R.string.title_last_care,
                        value.toInt()
                    )
                    else -> null
                }
            }
        }
        typedArray.recycle()
    }

    fun setTitle(@StringRes titleRes: Int, interval: Int) {
        binding.intervalTitle.text = context.getString(titleRes, interval)
    }

    fun setSliderValue(value: Float) {
        binding.intervalSlider.value = value
    }

    fun getSliderValue(): Float = binding.intervalSlider.value
    private enum class BottomSheetItemType(val value: Int) {
        DEFAULT(0), LAST_CARE(1)
    }
}
