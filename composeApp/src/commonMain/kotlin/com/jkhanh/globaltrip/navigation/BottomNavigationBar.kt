package com.jkhanh.globaltrip.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Bottom navigation bar for the app
 */
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 8.dp
    ) {
        NavigationItems.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.screen.route,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Data class for navigation item
 */
private data class NavigationItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector
)

/**
 * List of navigation items
 */
private val NavigationItems = listOf(
    NavigationItem(
        screen = Screen.Trips,
        title = "Trips",
        icon = Icons.Default.Home
    ),
    NavigationItem(
        screen = Screen.Maps,
        title = "Maps",
        icon = Icons.Default.Place
    ),
    NavigationItem(
        screen = Screen.Expenses,
        title = "Expenses",
        icon = Icons.Default.DateRange
    ),
    NavigationItem(
        screen = Screen.Settings,
        title = "Settings",
        icon = Icons.Default.Settings
    )
)
