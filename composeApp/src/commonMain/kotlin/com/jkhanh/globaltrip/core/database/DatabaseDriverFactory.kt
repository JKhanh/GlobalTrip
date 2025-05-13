package com.jkhanh.globaltrip.core.database

import app.cash.sqldelight.db.SqlDriver

/**
 * Platform-specific database driver factory.
 */
expect class DatabaseDriverFactory() {
    fun createDriver(): SqlDriver
}
