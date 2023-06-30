package com.anonlatte.florarium.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.anonlatte.florarium.R
import com.anonlatte.florarium.data.model.ScheduleType
import com.anonlatte.florarium.databinding.ListItemCareScheduleBinding
import com.anonlatte.florarium.ui.creation.CareScheduleItemData
import timber.log.Timber

class CareScheduleItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    lateinit var state: CareScheduleItemData
        private set


    val binding = ListItemCareScheduleBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        context.obtainStyledAttributes(attrs, R.styleable.CareScheduleItem).use {
            @StringRes val title: Int = it.getResourceId(R.styleable.CareScheduleItem_title, 0)
            @DrawableRes val icon: Int = it.getResourceId(R.styleable.CareScheduleItem_icon, 0)
            @StringRes val scheduleValue: Int =
                it.getResourceId(R.styleable.CareScheduleItem_scheduleValue, 0)
            val scheduleItemTypeId: Int =
                it.getInt(R.styleable.CareScheduleItem_scheduleItemType, 0)
            val scheduleType = checkNotNull(ScheduleType.toScheduleType(scheduleItemTypeId)) {
                Timber.e("Unknown schedule type: $scheduleItemTypeId")
            }

            updateData(
                CareScheduleItemData(
                    title = title,
                    icon = icon,
                    scheduleValue = scheduleValue,
                    scheduleItemType = scheduleType
                )
            )
        }
        updateUi()
    }

    fun updateData(data: CareScheduleItemData) {
        state = data
        // TODO: update UI
    }

    fun setItemDescription(
        defaultIntervalValue: Int?,
        lastCareValue: Int?
    ) {
        binding.scheduleItemDescription.text = formattedScheduleValue(
            defaultIntervalValue,
            lastCareValue
        )
    }

    private fun updateUi() = with(state) {
        binding.scheduleItemTitle.text = context.getString(title)
        binding.scheduleItemIcon.setImageDrawable(ContextCompat.getDrawable(context, icon))
        binding.scheduleItemDescription.text = context.getString(scheduleValue)
    }

    private fun formattedScheduleValue(
        defaultIntervalValue: Int?,
        lastCareValue: Int?
    ): String = if (lastCareValue != null && lastCareValue > 0) {
        "$lastCareValue $defaultIntervalValue"
    } else {
        defaultIntervalValue.toString()
    }
}
