package com.jkhanh.globaltrip.core.database

import app.cash.sqldelight.db.SqlDriver

/**
 * Web/WASM-specific implementation of DatabaseDriverFactory.
 * For WASM JS, we won't use a local database, so this is just a placeholder.
 */
actual class DatabaseDriverFactory {
    /**
     * Creates a null driver for WASM JS since we won't use a local database.
     * The repository will be overridden to fetch from remote API instead.
     */
    actual fun createDriver(): SqlDriver {
        throw UnsupportedOperationException("Local database is not supported for WASM JS target")
    }
}
