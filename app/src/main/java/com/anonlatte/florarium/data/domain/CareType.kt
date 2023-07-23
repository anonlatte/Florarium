package com.anonlatte.florarium.data.domain

enum class CareType {
    WATERING, SPRAYING, FERTILIZING, ROTATING;

    companion object {
        fun toScheduleType(value: Int?) = values().firstOrNull { it.ordinal == value }
    }
}
