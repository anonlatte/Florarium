package com.anonlatte.florarium.ui.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getStringOrThrow
import com.anonlatte.florarium.R
import kotlinx.android.synthetic.main.list_item_schedule.view.*

class CareScheduleItem(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CareScheduleItem)
    val title: String?
    val icon: Drawable?
    private var scheduleValue: String?
    val scheduleItemType: Int?

    init {
        inflate(context, R.layout.list_item_schedule, this)
        title = typedArray.getStringOrThrow(R.styleable.CareScheduleItem_title)
        icon = typedArray.getDrawable(R.styleable.CareScheduleItem_icon)
        scheduleValue = typedArray.getString(R.styleable.CareScheduleItem_scheduleValue)
        scheduleItemType = typedArray.getInt(R.styleable.CareScheduleItem_scheduleItemType, 0)
        wateringItemTitle.text = title
        wateringIcon.setImageDrawable(icon)
        wateringItemValue.text = scheduleValue
        typedArray.recycle()
    }
}
