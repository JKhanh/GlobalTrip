package com.jkhanh.globaltrip.core.domain.repository

import com.jkhanh.globaltrip.core.domain.model.Expense
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Expense operations
 */
interface ExpenseRepository {
    /**
     * Gets all expenses for a trip
     */
    fun getExpensesForTrip(tripId: String): Flow<List<Expense>>
    
    /**
     * Gets an expense by ID
     */
    suspend fun getExpenseById(id: String): Expense?
    
    /**
     * Creates a new expense
     */
    suspend fun createExpense(expense: Expense): String
    
    /**
     * Updates an existing expense
     */
    suspend fun updateExpense(expense: Expense)
    
    /**
     * Deletes an expense
     */
    suspend fun deleteExpense(id: String)
    
    /**
     * Gets the total expense amount for a trip in the trip's default currency
     */
    fun getTotalExpenseForTrip(tripId: String): Flow<Double>
    
    /**
     * Gets expenses grouped by category for a trip
     */
    fun getExpensesByCategory(tripId: String): Flow<Map<String, Double>>
}
