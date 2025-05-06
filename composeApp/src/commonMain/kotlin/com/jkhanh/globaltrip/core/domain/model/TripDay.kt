package com.jkhanh.globaltrip.core.domain.model

import kotlinx.datetime.LocalDate

/**
 * Represents a day within a trip
 */
data class TripDay(
    val id: String,
    val tripId: String,
    val date: LocalDate,
    val note: String = "",
    val activitiesIds: List<String> = emptyList()
)
