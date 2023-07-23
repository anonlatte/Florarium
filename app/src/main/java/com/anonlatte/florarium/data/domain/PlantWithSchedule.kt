package com.anonlatte.florarium.data.domain

import android.os.Parcelable
import com.anonlatte.florarium.app.utils.TimeStampHelper.daysAsMillis
import kotlinx.parcelize.Parcelize


@Parcelize
data class PlantWithSchedule(
    val plant: Plant,
    val schedule: RegularSchedule,
    val careHolder: CareHolder,
) : Parcelable {

    fun getRequiredCareTypes(currentTimestamp: Long): List<CareType> {
        val requiredCareTypes = mutableListOf<CareType>()
        if (isWateringRequired(currentTimestamp)) {
            requiredCareTypes.add(CareType.WATERING)
        }
        if (isFertilizingRequired(currentTimestamp)) {
            requiredCareTypes.add(CareType.FERTILIZING)
        }
        if (isSprayingRequired(currentTimestamp)) {
            requiredCareTypes.add(CareType.SPRAYING)
        }
        if (isRotatingRequired(currentTimestamp)) {
            requiredCareTypes.add(CareType.ROTATING)
        }
        return requiredCareTypes
    }

    private fun isWateringRequired(currentTimestamp: Long): Boolean {
        return getNextActionTime(schedule.wateringInterval, careHolder.wateredAt) < currentTimestamp
    }

    private fun isFertilizingRequired(currentTimestamp: Long): Boolean {
        return getNextActionTime(
            schedule.fertilizingInterval,
            careHolder.fertilizedAt
        ) < currentTimestamp
    }

    private fun isSprayingRequired(currentTimestamp: Long): Boolean {
        return getNextActionTime(schedule.sprayingInterval, careHolder.sprayedAt) < currentTimestamp
    }

    private fun isRotatingRequired(currentTimestamp: Long): Boolean {
        return getNextActionTime(schedule.rotatingInterval, careHolder.rotatedAt) < currentTimestamp
    }

    private fun getNextActionTime(
        interval: Int,
        lastCareTimeStamp: Long,
    ): Long = lastCareTimeStamp + interval.daysAsMillis
}