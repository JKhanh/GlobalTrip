package com.jkhanh.globaltrip.feature.trips.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.Trip
import kotlinx.datetime.LocalDate

/**
 * Use case for validating trip data according to business rules
 */
class ValidateTripUseCase {
    
    /**
     * Validates trip creation data
     */
    fun validateTripCreation(
        title: String,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): ValidationResult {
        return when {
            title.isBlank() -> {
                ValidationResult(false, "Title is required")
            }
            startDate == null -> {
                ValidationResult(false, "Start date is required")
            }
            endDate == null -> {
                ValidationResult(false, "End date is required")
            }
            startDate > endDate -> {
                ValidationResult(false, "Start date cannot be after end date")
            }
            else -> {
                ValidationResult(true, null)
            }
        }
    }
    
    /**
     * Validates a complete trip for update
     */
    fun validateTrip(trip: Trip): ValidationResult {
        return validateTripCreation(
            title = trip.title,
            startDate = trip.startDate,
            endDate = trip.endDate
        )
    }
}

/**
 * Result of validation containing success status and error message
 */
data class ValidationResult(
    val isValid: Boolean,
    val error: String?
)