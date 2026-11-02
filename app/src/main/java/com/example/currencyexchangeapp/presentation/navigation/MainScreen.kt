package com.example.currencyexchangeapp.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.currencyexchangeapp.presentation.screens.*
import com.example.currencyexchangeapp.presentation.viewmodel.CurrencyViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun MainScreen(windowSize: WindowSizeClass) {
    val navController = rememberNavController()
    val bottomNavigationItems = listOf(
        Screen.Home,
        Screen.Favorites,
        Screen.Settings
    )
    val viewModel: CurrencyViewModel = koinViewModel()

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = state.refreshInterval) {
        viewModel.scheduleBackgroundUpdates(context, state.refreshInterval)
    }

    val isExpanded = windowSize.widthSizeClass != WindowWidthSizeClass.Compact
    val selectedCurrencyCode by viewModel.selectedCurrencyCode.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomNavigationItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            screen.icon?.let { Icon(imageVector = it, contentDescription = screen.title) }
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                if (isExpanded) {
                    // tablet - dwie kol
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(0.4f)) {
                            CurrencyListScreen(
                                viewModel = viewModel,
                                onNavigateToDetails = { currencyCode -> viewModel.selectCurrency(currencyCode) }
                            )
                        }
                        Box(modifier = Modifier.weight(0.6f)) {
                            if (selectedCurrencyCode != null) {
                                CurrencyDetailsScreen(
                                    currencyCode = selectedCurrencyCode,
                                    viewModel = viewModel,
                                    onBackClick = { viewModel.selectCurrency(null) }
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Wybierz walutę z listy, aby zobaczyć szczegóły")
                                }
                            }
                        }
                    }
                } else {
                    CurrencyListScreen(
                        viewModel = viewModel,
                        onNavigateToDetails = { currencyCode ->
                            navController.navigate(Screen.Details.createRoute(currencyCode))
                        }
                    )
                }
            }

            composable(Screen.Favorites.route) {
                if (isExpanded) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(0.4f)) {
                            FavoritesScreen(
                                viewModel = viewModel,
                                onNavigateToDetails = { currencyCode -> viewModel.selectCurrency(currencyCode) }
                            )
                        }
                        Box(modifier = Modifier.weight(0.6f)) {
                            if (selectedCurrencyCode != null) {
                                CurrencyDetailsScreen(
                                    currencyCode = selectedCurrencyCode,
                                    viewModel = viewModel,
                                    onBackClick = { viewModel.selectCurrency(null) }
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Wybierz walutę z ulubionych, aby zobaczyć szczegóły")
                                }
                            }
                        }
                    }
                } else {
                    FavoritesScreen(
                        viewModel = viewModel,
                        onNavigateToDetails = { currencyCode ->
                            navController.navigate(Screen.Details.createRoute(currencyCode))
                        }
                    )
                }
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }

            composable(Screen.Details.route) { backStackEntry ->
                val currencyCode = backStackEntry.arguments?.getString("currencyCode")
                CurrencyDetailsScreen(
                    currencyCode = currencyCode,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}