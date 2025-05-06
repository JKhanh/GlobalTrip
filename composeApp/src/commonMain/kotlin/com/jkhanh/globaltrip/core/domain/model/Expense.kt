package com.jkhanh.globaltrip.core.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Represents a travel expense
 */
data class Expense(
    val id: String,
    val tripId: String,
    val activityId: String? = null,
    val title: String,
    val amount: Double,
    val currency: String,
    val category: ExpenseCategory,
    val date: LocalDate,
    val paymentMethod: String? = null,
    val attachmentUrl: String? = null,
    val notes: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val paidByUserId: String
)

enum class ExpenseCategory {
    TRANSPORTATION,
    ACCOMMODATION,
    FOOD,
    ACTIVITY,
    SHOPPING,
    OTHER
}
