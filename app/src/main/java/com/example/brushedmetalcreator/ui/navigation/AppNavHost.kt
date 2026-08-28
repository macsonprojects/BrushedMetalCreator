package com.example.brushedmetalcreator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.brushedmetalcreator.ui.feature_about.AboutScreen
import com.example.brushedmetalcreator.ui.feature_editor.EditorScreen
import com.example.brushedmetalcreator.ui.feature_settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.EDITOR,
    ) {
        // Editor Screen
        composable(Destinations.EDITOR) {
            EditorScreen(
                onNavigateToAbout = {
                    navController.navigate(Destinations.ABOUT)
                },
                onNavigateToSettings = {
                    navController.navigate(Destinations.SETTINGS)
                }
            )
        }

        // About Screen
        composable(Destinations.ABOUT) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Settings Screen
        composable(Destinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}