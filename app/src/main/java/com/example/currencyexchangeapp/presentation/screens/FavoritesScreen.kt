package com.example.currencyexchangeapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.currencyexchangeapp.presentation.viewmodel.CurrencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: CurrencyViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val favoriteCurrencies = state.currencies.filter { it.isFavorite }
    val filteredCurrencies = favoriteCurrencies.filter {
        it.code.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ulubione Waluty") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("Szukaj waluty (np. USD)") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Szukaj")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Wyczyść")
                        }
                    }
                },
                singleLine = true
            )

            if (favoriteCurrencies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Nie obserwujesz jeszcze żadnych walut.\nDodaj je klikając serce na liście głównej.",
                        modifier = Modifier.padding(32.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (filteredCurrencies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Brak wyników dla \"$searchQuery\"",
                        modifier = Modifier.padding(32.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCurrencies) { currency ->
                        val historyRate = state.historicalRatesList[currency.code]?.lastOrNull()?.rate

                        CurrencyListItem(
                            currency = currency,
                            decimalPlaces = state.decimalPlaces,
                            historicalRate = historyRate,
                            onFavoriteClick = {
                                viewModel.toggleFavorite(currency.code, false)
                            },
                            onClick = { onNavigateToDetails(currency.code) }
                        )
                    }
                }
            }
        }
    }
}