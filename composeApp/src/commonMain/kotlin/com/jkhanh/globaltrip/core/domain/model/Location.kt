package com.jkhanh.globaltrip.core.domain.model

/**
 * Represents a geographic location
 */
data class Location(
    val id: String,
    val name: String,
    val address: String = "",
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false,
    val notes: String = "",
    val placeId: String? = null // For map provider integration
)
