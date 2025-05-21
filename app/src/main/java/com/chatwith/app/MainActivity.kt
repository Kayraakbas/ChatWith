package com.chatwith.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.chatwith.app.ui.ChatScreen
import com.chatwith.app.ui.theme.ChatWithTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 🔁 Tema durumu burada tutulur
            var isDarkTheme by remember { mutableStateOf(false) }

            ChatWithTheme(darkTheme = isDarkTheme) {
                ChatWithApp(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme }
                )
            }

        }
    }
}


@Composable
fun ChatWithApp(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    var selectedAI by remember { mutableStateOf<String?>(null) }
    var showForgotPassword by remember { mutableStateOf(false) }
    val navController = rememberNavController()
    when {
        showForgotPassword -> {
            ForgotPasswordScreen(
                onBackToSignIn = { showForgotPassword = false },
                onResetClick = { showForgotPassword = false }
            )
        }

        isRegistering -> {
            RegisterScreen(
                onRegisterSuccess = {
                    isRegistering = false
                    isLoggedIn = false
                },
                onLoginClick = { isRegistering = false }
            )
        }

        !isLoggedIn -> {
            LoginScreen(
                onLoginSuccess = { isLoggedIn = true },
                onRegisterClick = { isRegistering = true },
                onForgotPasswordClick = { showForgotPassword = true }
            )
        }

        selectedAI != null -> {
            ChatScreen(
                selectedCharacter = selectedAI!!,
                onBack = { selectedAI = null },
                onLogout = { isLoggedIn = false }
            )
        }


        else -> {
            NavHost(
                navController = navController,
                startDestination = "main"
            ) {
                composable("main") {
                    BottomBarNavigation(
                        onLogout = {
                            isLoggedIn = false
                        },
                        onChatStart = { aiName ->
                            navController.navigate("chat_screen/$aiName")
                        },
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        navController = navController
                    )
                }

                composable(
                    route = "chat_screen/{character}",
                    arguments = listOf(navArgument("character") { type = NavType.StringType })
                ) { backStackEntry ->
                    val character = backStackEntry.arguments?.getString("character") ?: "AI"
                    ChatScreen(
                        selectedCharacter = character,
                        onBack = { navController.popBackStack() },
                        onLogout = { isLoggedIn = false }
                    )
                }

                composable("history_screen") {
                    HistoryScreen(
                        onBack = { navController.popBackStack() },
                        onResumeChat = { character ->
                            navController.navigate("chat_screen/$character")
                        }
                    )
                }
            }
        }
    }
}
