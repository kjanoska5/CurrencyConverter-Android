package com.example.currencyexchangeapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.currencyexchangeapp.presentation.viewmodel.CurrencyViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CurrencyViewModel
) {
    val state by viewModel.state.collectAsState()
    var apiKeyInput by remember { mutableStateOf("") }
    var baseCurrencyInput by remember { mutableStateOf(state.baseCurrency) }
    val scrollState = rememberScrollState()

    var expanded by remember { mutableStateOf(false) }

    var decimalPlaces by remember { mutableStateOf(4) }

    val intervalOptions = mapOf(
        15 to "Co 15 minut",
        60 to "Co 1 godzinę",
        360 to "Co 6 godzin",
        1440 to "Raz dziennie"
    )

    val isOnline = state.error == null
    val statusColor = if (isOnline) Color(0xFF4CAF50) else Color(0xFFF44336)
    val statusText = if (isOnline) "Online (Połączono)" else "Offline (Brak sieci)"
    val statusIcon = if (isOnline) Icons.Default.Check else Icons.Default.Warning

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustawienia") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = statusIcon, contentDescription = null, tint = statusColor)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Status: $statusText",
                        color = statusColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Zabezpieczenia i API", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Twój klucz zostanie bezpiecznie zaszyfrowany na urządzeniu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    label = { Text("Wklej klucz z ExchangeRate-API") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = { viewModel.saveApiKey(apiKeyInput) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Zapisz Klucz API")
                }
            }

            HorizontalDivider()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Preferencje", style = MaterialTheme.typography.titleMedium)

                Text(text = "Format wyświetlania kursu", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.decimalPlaces == 2,
                        onClick = { viewModel.updateDecimalPlaces(2) },
                        label = { Text("2 miejsca (np. 4.12)") }
                    )
                    FilterChip(
                        selected = state.decimalPlaces == 4,
                        onClick = { viewModel.updateDecimalPlaces(4) },
                        label = { Text("4 miejsca (np. 4.1234)") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = baseCurrencyInput,
                    onValueChange = { baseCurrencyInput = it.uppercase() },
                    label = { Text("Waluta bazowa (PLN, USD, EUR)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = { viewModel.updateBaseCurrency(baseCurrencyInput) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Zmień walutę i odśwież kursy")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Odświeżanie w tle", style = MaterialTheme.typography.titleMedium)

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = intervalOptions[state.refreshInterval] ?: "Nieznana",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pobieraj nowe dane") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        intervalOptions.forEach { (minutes, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    viewModel.updateRefreshInterval(minutes)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}