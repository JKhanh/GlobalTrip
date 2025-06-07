package com.jkhanh.globaltrip.di

import org.koin.core.context.startKoin
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
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(globalTripAppModule + platformModules)
}