package com.anonlatte.florarium.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.anonlatte.florarium.R
import com.anonlatte.florarium.data.domain.ScheduleType
import com.anonlatte.florarium.databinding.ListItemCareScheduleBinding
import com.anonlatte.florarium.ui.creation.CareScheduleItemData
import timber.log.Timber

class CareScheduleItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    lateinit var state: CareScheduleItemData
        private set


    private val binding = ListItemCareScheduleBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var onSwitchCheckedChangeListener: OnCheckedChangeListener? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.CareScheduleItem) {
            @StringRes
            val title: Int = getResourceId(R.styleable.CareScheduleItem_title, 0)

            @DrawableRes
            val icon: Int = getResourceId(R.styleable.CareScheduleItem_icon, 0)

            val scheduleItemTypeId: Int = getInt(
                R.styleable.CareScheduleItem_scheduleItemType,
                0
            )
            val scheduleType = checkNotNull(ScheduleType.toScheduleType(scheduleItemTypeId)) {
                Timber.e("Unknown schedule type: $scheduleItemTypeId")
            }

            updateData(
                CareScheduleItemData(
                    title = title,
                    icon = icon,
                    scheduleItemType = scheduleType
                )
            )
        }
        isSaveEnabled = true
    }

    fun updateData(data: CareScheduleItemData) {
        state = data.copy(
            lastCareValue = if (data.intervalValue == 0) 0 else data.lastCareValue,
        )
        updateUi()
    }

    fun updateData(data: (CareScheduleItemData) -> CareScheduleItemData) {
        updateData(data(state))
    }

    fun setOnSwitchCheckedChangeListener(listener: (Boolean) -> Unit) {
        onSwitchCheckedChangeListener = OnCheckedChangeListener { _, isChecked ->
            listener(isChecked)
        }
        binding.itemSwitch.setOnCheckedChangeListener(onSwitchCheckedChangeListener)

    }

    private fun setItemDescription(
        defaultIntervalValue: Int?,
        lastCareValue: Int?,
    ) {
        binding.scheduleItemDescription.text = if (defaultIntervalValue == 0) {
            context.getString(R.string.value_not_set)
        } else {
            formattedScheduleValue(
                defaultIntervalValue,
                lastCareValue
            )
        }
    }

    private fun updateUi() = with(state) {
        if (title != 0) {
            binding.scheduleItemTitle.text = context.getString(title)
        }
        if (icon != 0) {
            binding.scheduleItemIcon.setImageDrawable(ContextCompat.getDrawable(context, icon))
        }

        setItemDescription(intervalValue, lastCareValue)

        binding.itemSwitch.setOnCheckedChangeListener(null)
        binding.itemSwitch.isChecked = intervalValue > 0
        binding.itemSwitch.setOnCheckedChangeListener(onSwitchCheckedChangeListener)
    }

    private fun formattedScheduleValue(
        defaultIntervalValue: Int?,
        lastCareValue: Int?,
    ): String = if (lastCareValue != null && lastCareValue > 0) {
        "$lastCareValue $defaultIntervalValue"
    } else {
        defaultIntervalValue.toString()
    }
}
