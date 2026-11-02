package com.example.currencyexchangeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import com.example.currencyexchangeapp.presentation.navigation.MainScreen
import com.example.currencyexchangeapp.ui.theme.CurrencyExchangeAppTheme
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSize = calculateWindowSizeClass(this)
            CurrencyExchangeAppTheme {
                MainScreen(windowSize)
            }
        }
    }
}