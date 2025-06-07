package com.jkhanh.globaltrip.di

import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val globalTripAppModule = module {
    includes(
        networkModule,
        coreModule,
        repositoryModule,
        authModule,
        tripsModule,
        settingsModule
    )
}

/**
 * Initialize Koin DI for GlobalTrip app
 * This function ensures Koin is only initialized once
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    try {
        startKoin {
            appDeclaration()
            modules(globalTripAppModule + platformModules)
        }
    } catch (e: KoinApplicationAlreadyStartedException) {
        // Koin already started, ignore the exception
        // This handles the case where Koin is already initialized
    }
}