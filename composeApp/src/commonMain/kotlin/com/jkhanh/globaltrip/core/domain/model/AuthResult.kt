package com.jkhanh.globaltrip.core.domain.model

/**
 * Represents the result of authentication operations
 */
sealed interface AuthResult<out T> {
    /**
     * Successful authentication operation
     */
    data class Success<T>(val data: T) : AuthResult<T>
    
    /**
     * Failed authentication operation with error details
     */
    data class Error(val error: AuthError) : AuthResult<Nothing>
}

/**
 * Extension function to check if result is successful
 */
fun <T> AuthResult<T>.isSuccess(): Boolean = this is AuthResult.Success

/**
 * Extension function to check if result is error
 */
fun <T> AuthResult<T>.isError(): Boolean = this is AuthResult.Error

/**
 * Extension function to get data if successful, null otherwise
 */
fun <T> AuthResult<T>.getDataOrNull(): T? = when (this) {
    is AuthResult.Success -> data
    is AuthResult.Error -> null
}

/**
 * Extension function to get error if failed, null otherwise
 */
fun <T> AuthResult<T>.getErrorOrNull(): AuthError? = when (this) {
    is AuthResult.Success -> null
    is AuthResult.Error -> error
}