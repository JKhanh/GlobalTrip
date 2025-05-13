package com.jkhanh.globaltrip.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS-specific implementation of DatabaseDriverFactory.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = GlobalTripDatabase.Schema,
            name = "globaltrip.db"
        )
    }
}
