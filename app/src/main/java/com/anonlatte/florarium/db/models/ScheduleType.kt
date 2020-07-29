package com.anonlatte.florarium.db.models

enum class ScheduleType(value: Int) {
    WATERING(0), SPRAYING(1), FERTILIZING(2), ROTATING(3);

    companion object {
        fun toScheduleType(value: Int?) = values().firstOrNull { it.ordinal == value }
    }
}
