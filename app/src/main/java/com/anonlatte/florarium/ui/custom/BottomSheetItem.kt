package com.anonlatte.florarium.ui.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.getStringOrThrow
import com.anonlatte.florarium.R
import com.anonlatte.florarium.databinding.ListItemBottomSheetBinding

class BottomSheetItem(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomSheetItem)
    val title: String?
    val icon: Drawable?
    val itemType: Int

    val binding = ListItemBottomSheetBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        inflate(context, R.layout.list_item_bottom_sheet, this)
        title = typedArray.getStringOrThrow(R.styleable.BottomSheetItem_title)
        icon = typedArray.getDrawable(R.styleable.BottomSheetItem_icon)
        itemType = typedArray.getInt(
            R.styleable.BottomSheetItem_item_type,
            BottomSheetItemType.DEFAULT.value
        )
        binding.textIntervalTitle.text = title
        binding.imageIconInterval.setImageDrawable(icon)
        binding.sliderInterval.addOnChangeListener { _, value, _ ->
            binding.textIntervalTitle.text = when (itemType) {
                BottomSheetItemType.DEFAULT.value -> context.getString(
                    R.string.title_interval_in_days,
                    value.toInt()
                )
                BottomSheetItemType.WINTER.value -> context.getString(
                    R.string.title_interval_for_winter,
                    value.toInt()
                )
                BottomSheetItemType.LAST_CARE.value -> context.getString(
                    R.string.title_last_care,
                    value.toInt()
                )
                else -> null
            }
        }
        typedArray.recycle()
    }

    fun setTitle(value: String) {
        binding.textIntervalTitle.text = value
    }

    fun setSliderValue(value: Float) {
        binding.sliderInterval.value = value
    }

    fun getSliderValue(): Float = binding.sliderInterval.value

    private enum class BottomSheetItemType(val value: Int) {
        DEFAULT(0), WINTER(1), LAST_CARE(2)
    }
}
