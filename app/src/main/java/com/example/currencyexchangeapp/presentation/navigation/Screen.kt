package com.example.currencyexchangeapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Home : Screen("home", "Kursy", Icons.Default.List)
    object Favorites : Screen("favorites", "Ulubione", Icons.Default.Favorite)
    object Settings : Screen("settings", "Ustawienia", Icons.Default.Settings)
    object Details : Screen("details/{currencyCode}", "Szczegóły", null) {
        fun createRoute(currencyCode: String) = "details/$currencyCode"
    }
}