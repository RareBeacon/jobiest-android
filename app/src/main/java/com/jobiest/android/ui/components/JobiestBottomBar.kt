package com.jobiest.android.ui.components

import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jobiest.android.ui.navigation.Screen
import com.jobiest.android.ui.theme.*

private data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun JobiestBottomBar(navController: NavController) {
    // 5 Mobile Bottom Navigation Destinations (from Jobiest-Screen-Architecture.md)
    val items = listOf(
        NavItem(Screen.Dashboard, "Overview", Icons.Default.Home),
        NavItem(Screen.Jobs, "Discover", Icons.Default.Search),
        NavItem(Screen.Applications, "Applications", Icons.Default.Layers),
        NavItem(Screen.ResumeStudio, "Resume", Icons.Default.AutoAwesome),
        NavItem(Screen.Profile, "Profile", Icons.Default.Person)
    )


    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = JobiestBg,
        contentColor = JobiestInk,
        tonalElevation = 0.dp,
        modifier = Modifier.border(
            width = 1.dp,
            color = JobiestBorder
        )
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.screen.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            popUpTo(Screen.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = JobiestInk,
                    selectedTextColor = JobiestInk,
                    indicatorColor = JobiestBrandSoft,
                    unselectedIconColor = JobiestMuted,
                    unselectedTextColor = JobiestMuted
                )
            )
        }
    }
}
