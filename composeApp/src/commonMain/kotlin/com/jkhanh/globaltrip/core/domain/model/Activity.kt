package com.jkhanh.globaltrip.core.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

/**
 * Represents an activity within a trip day
 */
data class Activity(
    val id: String,
    val tripId: String,
    val tripDayId: String,
    val title: String,
    val description: String = "",
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val location: Location? = null,
    val category: ActivityCategory = ActivityCategory.OTHER,
    val reminderTime: LocalDateTime? = null,
)

enum class ActivityCategory {
    TRANSPORTATION,
    ACCOMMODATION,
    FOOD,
    SIGHTSEEING,
    MEETING,
    OTHER
}
