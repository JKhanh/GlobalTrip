package com.jkhanh.globaltrip.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Simple navigator for handling screen transitions
 */
class Navigator {
    var currentScreen by mutableStateOf<Screen>(Screen.Trips)
        private set
    
    private val backStack = mutableListOf<Screen>()
    
    fun navigateTo(screen: Screen) {
        if (currentScreen != screen) {
            backStack.add(currentScreen)
            currentScreen = screen
        }
    }
    
    fun navigateBack(): Boolean {
        return if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeAt(backStack.lastIndex)
            true
        } else {
            false
        }
    }
}

/**
 * Create and remember a Navigator instance
 */
@Composable
fun rememberNavigator(): Navigator {
    return remember { Navigator() }
}
