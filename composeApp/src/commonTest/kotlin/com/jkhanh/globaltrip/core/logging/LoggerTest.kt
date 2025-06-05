package com.jkhanh.globaltrip.core.logging

import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Basic test to verify Logger functionality
 */
class LoggerTest {
    
    companion object {
        private val TAG = LoggerTest::class.simpleName
    }
    
    @Test
    fun testLoggerMethods() {
        // Test that logger methods don't crash
        Logger.v("Verbose message")
        Logger.d("Debug message")
        Logger.i("Info message")
        Logger.w("Warning message")
        Logger.e("Error message")
        
        // Test with class name tag using TAG variable
        Logger.d("Debug message with class tag", TAG)
        Logger.i("Info message with class tag", TAG)
        
        // Test with exception
        val exception = RuntimeException("Test exception")
        Logger.e("Error with exception", TAG, exception)
        
        // If we get here without crashing, the test passes
        assertNotNull(Logger)
    }
}