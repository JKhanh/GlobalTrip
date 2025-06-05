package com.jkhanh.globaltrip.core.data.storage

import com.jkhanh.globaltrip.core.domain.repository.SecureStorage

/**
 * Mock implementation of SecureStorage for common code and testing
 * Platform-specific implementations will override this with secure storage
 */
class MockSecureStorage : SecureStorage {
    
    private val storage = mutableMapOf<String, String>()
    
    override suspend fun saveToken(key: String, token: String) {
        storage[key] = token
    }
    
    override suspend fun getToken(key: String): String? {
        return storage[key]
    }
    
    override suspend fun deleteToken(key: String) {
        storage.remove(key)
    }
    
    override suspend fun clearAll() {
        storage.clear()
    }
    
    override suspend fun hasToken(key: String): Boolean {
        return storage.containsKey(key)
    }
}