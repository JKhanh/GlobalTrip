package com.jkhanh.globaltrip.di

import kotlin.test.Test
import org.koin.test.verify.verify

/**
 * TDD: RED phase - These tests should FAIL initially
 * Testing DI module verification using Koin 4.0's verify() API
 */
class KoinModuleVerificationTest {
    
    @Test
    fun `verify core module dependencies`() {
        // ❌ FAIL - coreModule doesn't exist yet
        coreModule.verify()
    }
    
    @Test 
    fun `verify repository module dependencies`() {
        // ❌ FAIL - repositoryModule doesn't exist yet
        repositoryModule.verify()
    }
    
    @Test
    fun `verify network module dependencies`() {
        // ❌ FAIL - networkModule doesn't exist yet
        networkModule.verify()
    }
    
    @Test
    fun `verify all core modules together`() {
        // ✅ PASS - Modules now exist
        networkModule.verify()
        coreModule.verify()
        repositoryModule.verify()
    }
}