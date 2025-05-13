package com.jkhanh.globaltrip.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jkhanh.globaltrip.core.app.GlobalTripAndroidApp

/**
 * Android-specific implementation of DatabaseDriverFactory.
 */
actual class DatabaseDriverFactory {
    // Parameterized constructor for flexibility
    constructor(context: Context) {
        this.context = context
    }
    
    // Default constructor to match expect
    actual constructor() {
        this.context = GlobalTripAndroidApp.appContext
    }
    
    private val context: Context
    
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = GlobalTripDatabase.Schema,
            context = context,
            name = "globaltrip.db"
        )
    }
}
