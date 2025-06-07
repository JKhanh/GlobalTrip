package com.jkhanh.globaltrip.core.database

/**
 * Provides a singleton instance of the database.
 */
class DatabaseProvider(private val driverFactory: DatabaseDriverFactory) {
    
    /**
     * Lazy-initialized database instance.
     */
    val database: GlobalTripDatabase by lazy {
        val driver = driverFactory.createDriver()
        GlobalTripDatabase(driver)
    }
}

