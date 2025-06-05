package com.jkhanh.globaltrip.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents an authenticated user in the GlobalTrip application
 */
@Serializable
data class AuthUser(
    val id: String,
    val email: String,
    val name: String? = null,
    val avatarUrl: String? = null,
    val createdAt: String? = null,
    val lastSignInAt: String? = null,
    val isEmailVerified: Boolean = false
)