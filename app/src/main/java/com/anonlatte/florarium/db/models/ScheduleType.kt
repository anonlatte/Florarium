package com.anonlatte.florarium.db.models

enum class ScheduleType {
    WATERING, SPRAYING, FERTILIZING, ROTATING;

    companion object {
        fun toScheduleType(value: Int?) = values().firstOrNull { it.ordinal == value }
    }
}
