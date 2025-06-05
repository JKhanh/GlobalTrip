package com.jkhanh.globaltrip.di

import android.content.Context
import com.jkhanh.globaltrip.core.database.DatabaseDriverFactory
import com.jkhanh.globaltrip.core.database.DatabaseProvider
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific dependencies
 */
actual val platformModules: List<Module> = listOf(
    module {
        single { DatabaseDriverFactory(get<Context>()) }
        single { DatabaseProvider(get()) }
    }
)
