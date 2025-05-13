package com.jkhanh.globaltrip.di

import com.jkhanh.globaltrip.core.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS-specific dependencies
 */
actual val platformModules: List<Module> = listOf(
    module {
        single { DatabaseDriverFactory() }
    }
)
