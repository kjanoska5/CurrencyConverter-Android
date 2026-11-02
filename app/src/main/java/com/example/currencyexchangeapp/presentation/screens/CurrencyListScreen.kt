package com.example.currencyexchangeapp.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.currencyexchangeapp.data.local.CurrencyEntity
import com.example.currencyexchangeapp.presentation.viewmodel.CurrencyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyListScreen(
    viewModel: CurrencyViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kursy Walut (${state.baseCurrency})") },
                actions = {
                    IconButton(onClick = { viewModel.refreshRates(simulateDeltas = true) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Odśwież")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.isLoading && state.currencies.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.currencies.isEmpty()) {
                Text(
                    text = state.error ?: "Wystąpił błąd",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {

                    if (state.error != null && state.currencies.isNotEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Tryb offline. Pokazuję zapisane dane.",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            )
                        }
                    }

                    if (state.lastUpdateTimestamp > 0L) {
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val dateString = dateFormat.format(Date(state.lastUpdateTimestamp))

                        Text(
                            text = "Ostatnia aktualizacja: $dateString",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            textAlign = TextAlign.End
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.currencies) { currency ->
                            // wyciagniecie kursu
                            val historyRate = state.historicalRatesList[currency.code]?.lastOrNull()?.rate

                            CurrencyListItem(
                                currency = currency,
                                decimalPlaces = state.decimalPlaces,
                                historicalRate = historyRate,
                                onFavoriteClick = {
                                    viewModel.toggleFavorite(currency.code, !currency.isFavorite)
                                },
                                onClick = { onNavigateToDetails(currency.code) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyListItem(
    currency: CurrencyEntity,
    decimalPlaces: Int,
    historicalRate: Double?,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    val percentageChange = remember(currency.rate, historicalRate) {
        if (historicalRate != null && historicalRate > 0.0) {
            ((currency.rate - historicalRate) / historicalRate) * 100
        } else {
            0.0
        }
    }

    val trendColor = when {
        percentageChange > 0 -> Color(0xFF4CAF50) // Zielony
        percentageChange < 0 -> Color(0xFFF44336) // Czerwony
        else -> Color.Gray
    }

    val trendIcon: ImageVector? = when {
        percentageChange > 0 -> Icons.Default.KeyboardArrowUp
        percentageChange < 0 -> Icons.Default.KeyboardArrowDown
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                val formatString = "%.${decimalPlaces}f"
                Text(
                    text = String.format(Locale.US, formatString, currency.rate),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (trendIcon != null) {
                    Icon(
                        imageVector = trendIcon,
                        contentDescription = "Trend",
                        tint = trendColor
                    )
                }
                Text(
                    text = String.format(Locale.US, "%.2f%%", abs(percentageChange)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = trendColor
                )
                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (currency.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Zarządzaj ulubionymi",
                        tint = if (currency.isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}