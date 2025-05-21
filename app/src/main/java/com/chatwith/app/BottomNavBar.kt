package com.chatwith.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BottomBarNavigation(
    onLogout: () -> Unit,
    onChatStart: (String) -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    navController: NavHostController
) {
    var selectedTab by remember { mutableStateOf(0) }
    val colors = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colors.background,
        bottomBar = {
            NavigationBar(
                containerColor = colors.surface,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text("Home")
                    },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.primary,
                        unselectedIconColor = colors.onSurface.copy(alpha = 0.6f),
                        selectedTextColor = colors.onSurface,
                        unselectedTextColor = colors.onSurface.copy(alpha = 0.6f),
                        indicatorColor = colors.primary.copy(alpha = 0.1f)
                    )
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account"
                        )
                    },
                    label = {
                        Text("Account")
                    },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.primary,
                        unselectedIconColor = colors.onSurface.copy(alpha = 0.6f),
                        selectedTextColor = colors.onSurface,
                        unselectedTextColor = colors.onSurface.copy(alpha = 0.6f),
                        indicatorColor = colors.primary.copy(alpha = 0.1f)
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            when (selectedTab) {
                0 -> HomeScreen(
                    onChatStart = onChatStart,
                    onHistoryClick = { /* TODO */ },
                    onProfileClick = { /* TODO */ },
                    onLogoutClick = onLogout
                )
                1 -> AccountScreen(
                    onLogout = onLogout,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onThemeToggle,
                    onChatHistoryClick = {
                        navController.navigate("history_screen")
                    }
                )
            }
        }
    }
}
