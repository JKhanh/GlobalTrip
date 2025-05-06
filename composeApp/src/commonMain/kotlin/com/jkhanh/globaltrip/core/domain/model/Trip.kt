package com.jkhanh.globaltrip.core.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Represents a travel trip with its details
 */
data class Trip(
    val id: String,
    val title: String,
    val description: String = "",
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val destination: String,
    val coverImageUrl: String? = null,
    val isArchived: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant,
    val ownerId: String,
    val collaboratorIds: List<String> = emptyList()
)
