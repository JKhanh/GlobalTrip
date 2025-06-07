package com.jkhanh.globaltrip.di

import org.koin.core.module.Module

/**
 * Get platform-specific modules
 */
expect val platformModules: List<Module>
