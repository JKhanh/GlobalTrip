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
 * Bottom navigation bar for the app using type-safe routes
 */
@Composable
fun BottomNavigationBar(
    currentRouteClass: Any?,
    onNavigate: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 8.dp
    ) {
        NavigationItems.forEach { item ->
            val isSelected = when (currentRouteClass) {
                is Trips -> item.destination is Trips
                is Maps -> item.destination is Maps
                is Expenses -> item.destination is Expenses
                is Settings -> item.destination is Settings
                else -> false
            }
            
            BottomNavigationItem(
                selected = isSelected,
                onClick = { onNavigate(item.destination) },
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
    val destination: Any,
    val title: String,
    val icon: ImageVector
)

/**
 * List of navigation items
 */
private val NavigationItems = listOf(
    NavigationItem(
        destination = Trips,
        title = "Trips",
        icon = Icons.Default.Home
    ),
    NavigationItem(
        destination = Maps,
        title = "Maps",
        icon = Icons.Default.Place
    ),
    NavigationItem(
        destination = Expenses,
        title = "Expenses",
        icon = Icons.Default.DateRange
    ),
    NavigationItem(
        destination = Settings,
        title = "Settings",
        icon = Icons.Default.Settings
    )
)
