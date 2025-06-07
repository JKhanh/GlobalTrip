package com.jkhanh.globaltrip.feature.trips.domain.usecase

import com.jkhanh.globaltrip.core.domain.model.Trip
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for ValidateTripUseCase
 */
class ValidateTripUseCaseTest {
    
    private val validateTripUseCase = ValidateTripUseCase()
    
    // Test data
    private val validTitle = "Trip to Paris"
    private val validStartDate = LocalDate(2024, 6, 15)
    private val validEndDate = LocalDate(2024, 6, 22)
    
    @Test
    fun `validateTripCreation should return valid when all fields are correct`() {
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = validTitle,
            startDate = validStartDate,
            endDate = validEndDate
        )
        
        // Then
        assertTrue(result.isValid)
        assertNull(result.error)
    }
    
    @Test
    fun `validateTripCreation should return invalid when title is blank`() {
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = "",
            startDate = validStartDate,
            endDate = validEndDate
        )
        
        // Then
        assertFalse(result.isValid)
        assertEquals("Title is required", result.error)
    }
    
    @Test
    fun `validateTripCreation should return invalid when title contains only whitespace`() {
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = "   ",
            startDate = validStartDate,
            endDate = validEndDate
        )
        
        // Then
        assertFalse(result.isValid)
        assertEquals("Title is required", result.error)
    }
    
    @Test
    fun `validateTripCreation should return invalid when start date is null`() {
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = validTitle,
            startDate = null,
            endDate = validEndDate
        )
        
        // Then
        assertFalse(result.isValid)
        assertEquals("Start date is required", result.error)
    }
    
    @Test
    fun `validateTripCreation should return invalid when end date is null`() {
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = validTitle,
            startDate = validStartDate,
            endDate = null
        )
        
        // Then
        assertFalse(result.isValid)
        assertEquals("End date is required", result.error)
    }
    
    @Test
    fun `validateTripCreation should return invalid when start date is after end date`() {
        // Given
        val startDate = LocalDate(2024, 6, 22)
        val endDate = LocalDate(2024, 6, 15)
        
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = validTitle,
            startDate = startDate,
            endDate = endDate
        )
        
        // Then
        assertFalse(result.isValid)
        assertEquals("Start date cannot be after end date", result.error)
    }
    
    @Test
    fun `validateTripCreation should return valid when start date equals end date`() {
        // Given
        val sameDate = LocalDate(2024, 6, 15)
        
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = validTitle,
            startDate = sameDate,
            endDate = sameDate
        )
        
        // Then
        assertTrue(result.isValid)
        assertNull(result.error)
    }
    
    @Test
    fun `validateTripCreation should return first validation error when multiple fields are invalid`() {
        // When - testing precedence of validation rules
        val result = validateTripUseCase.validateTripCreation(
            title = "", // Invalid
            startDate = null, // Invalid
            endDate = null // Invalid
        )
        
        // Then - should return the first error (title)
        assertFalse(result.isValid)
        assertEquals("Title is required", result.error)
    }
    
    @Test
    fun `validateTripCreation should prioritize date logic error over missing dates`() {
        // Given
        val startDate = LocalDate(2024, 6, 22)
        val endDate = LocalDate(2024, 6, 15)
        
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = validTitle,
            startDate = startDate,
            endDate = endDate
        )
        
        // Then - should catch date logic error
        assertFalse(result.isValid)
        assertEquals("Start date cannot be after end date", result.error)
    }
    
    @Test
    fun `validateTrip should return valid for valid trip object`() {
        // Given
        val validTrip = createValidTrip()
        
        // When
        val result = validateTripUseCase.validateTrip(validTrip)
        
        // Then
        assertTrue(result.isValid)
        assertNull(result.error)
    }
    
    @Test
    fun `validateTrip should return invalid for trip with blank title`() {
        // Given
        val invalidTrip = createValidTrip().copy(title = "")
        
        // When
        val result = validateTripUseCase.validateTrip(invalidTrip)
        
        // Then
        assertFalse(result.isValid)
        assertEquals("Title is required", result.error)
    }
    
    @Test
    fun `validateTrip should return invalid for trip with null start date`() {
        // Given
        val invalidTrip = createValidTrip().copy(startDate = null)
        
        // When
        val result = validateTripUseCase.validateTrip(invalidTrip)
        
        // Then
        assertFalse(result.isValid)
        assertEquals("Start date is required", result.error)
    }
    
    @Test
    fun `validateTrip should return invalid for trip with null end date`() {
        // Given
        val invalidTrip = createValidTrip().copy(endDate = null)
        
        // When
        val result = validateTripUseCase.validateTrip(invalidTrip)
        
        // Then
        assertFalse(result.isValid)
        assertEquals("End date is required", result.error)
    }
    
    @Test
    fun `validateTrip should return invalid for trip with start date after end date`() {
        // Given
        val invalidTrip = createValidTrip().copy(
            startDate = LocalDate(2024, 6, 22),
            endDate = LocalDate(2024, 6, 15)
        )
        
        // When
        val result = validateTripUseCase.validateTrip(invalidTrip)
        
        // Then
        assertFalse(result.isValid)
        assertEquals("Start date cannot be after end date", result.error)
    }
    
    @Test
    fun `validateTrip should return valid for trip with same start and end date`() {
        // Given
        val sameDate = LocalDate(2024, 6, 15)
        val validTrip = createValidTrip().copy(
            startDate = sameDate,
            endDate = sameDate
        )
        
        // When
        val result = validateTripUseCase.validateTrip(validTrip)
        
        // Then
        assertTrue(result.isValid)
        assertNull(result.error)
    }
    
    @Test
    fun `validateTrip should not validate optional fields like destination`() {
        // Given - trip with empty destination (should still be valid)
        val tripWithEmptyDestination = createValidTrip().copy(destination = "")
        
        // When
        val result = validateTripUseCase.validateTrip(tripWithEmptyDestination)
        
        // Then
        assertTrue(result.isValid)
        assertNull(result.error)
    }
    
    @Test
    fun `validateTrip should not validate optional fields like description`() {
        // Given - trip with empty description (should still be valid)
        val tripWithEmptyDescription = createValidTrip().copy(description = "")
        
        // When
        val result = validateTripUseCase.validateTrip(tripWithEmptyDescription)
        
        // Then
        assertTrue(result.isValid)
        assertNull(result.error)
    }
    
    // Edge cases
    @Test
    fun `validateTripCreation should handle very long title`() {
        // Given
        val veryLongTitle = "A".repeat(1000)
        
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = veryLongTitle,
            startDate = validStartDate,
            endDate = validEndDate
        )
        
        // Then - should be valid (no length restriction in business rules)
        assertTrue(result.isValid)
        assertNull(result.error)
    }
    
    @Test
    fun `validateTripCreation should handle dates far in the future`() {
        // Given
        val futureStartDate = LocalDate(2030, 1, 1)
        val futureEndDate = LocalDate(2030, 12, 31)
        
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = validTitle,
            startDate = futureStartDate,
            endDate = futureEndDate
        )
        
        // Then - should be valid (no date range restriction in business rules)
        assertTrue(result.isValid)
        assertNull(result.error)
    }
    
    @Test
    fun `validateTripCreation should handle dates in the past`() {
        // Given
        val pastStartDate = LocalDate(2020, 1, 1)
        val pastEndDate = LocalDate(2020, 12, 31)
        
        // When
        val result = validateTripUseCase.validateTripCreation(
            title = validTitle,
            startDate = pastStartDate,
            endDate = pastEndDate
        )
        
        // Then - should be valid (past trips are allowed)
        assertTrue(result.isValid)
        assertNull(result.error)
    }
    
    // Helper function to create a valid trip for testing
    private fun createValidTrip(): Trip {
        return Trip(
            id = "test-trip-id",
            title = validTitle,
            description = "A wonderful trip to Paris",
            startDate = validStartDate,
            endDate = validEndDate,
            destination = "Paris, France",
            coverImageUrl = null,
            isArchived = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
            ownerId = "test-user-id",
            collaboratorIds = emptyList()
        )
    }
}