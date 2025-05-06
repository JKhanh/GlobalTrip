package com.jkhanh.globaltrip.core.domain.model

import kotlinx.datetime.Instant

/**
 * Represents a task or checklist item for a trip
 */
data class Task(
    val id: String,
    val tripId: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val category: TaskCategory = TaskCategory.OTHER,
    val assignedToUserId: String? = null,
    val dueDate: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class TaskCategory {
    PACKING,
    BOOKING,
    DOCUMENT,
    PREPARATION,
    OTHER
}
