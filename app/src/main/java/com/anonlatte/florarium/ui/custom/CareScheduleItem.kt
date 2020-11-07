package com.anonlatte.florarium.ui.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getStringOrThrow
import com.anonlatte.florarium.R
import com.anonlatte.florarium.databinding.ListItemCareScheduleBinding

class CareScheduleItem(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CareScheduleItem)
    val title: String?
    val icon: Drawable?
    private var scheduleValue: String?
    val scheduleItemType: Int?

    val binding = ListItemCareScheduleBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        title = typedArray.getStringOrThrow(R.styleable.CareScheduleItem_title)
        icon = typedArray.getDrawable(R.styleable.CareScheduleItem_icon)
        scheduleValue = typedArray.getString(R.styleable.CareScheduleItem_scheduleValue)
        scheduleItemType = typedArray.getInt(R.styleable.CareScheduleItem_scheduleItemType, 0)
        binding.textScheduleItemTitle.text = title
        binding.scheduleItemIcon.setImageDrawable(icon)
        binding.textScheduleItemDescription.text = scheduleValue
        typedArray.recycle()
    }

    fun setItemDescription(value: String) {
        binding.textScheduleItemDescription.text = value
    }
}
