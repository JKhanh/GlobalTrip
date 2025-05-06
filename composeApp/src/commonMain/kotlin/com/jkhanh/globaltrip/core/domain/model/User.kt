package com.jkhanh.globaltrip.core.domain.model

/**
 * Represents a user of the application
 */
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val profilePictureUrl: String? = null,
    val isEmailVerified: Boolean = false
)
