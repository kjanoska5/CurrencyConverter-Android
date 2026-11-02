package com.example.currencyexchangeapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.currencyexchangeapp.presentation.viewmodel.CurrencyViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDetailsScreen(
    currencyCode: String?,
    viewModel: CurrencyViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val currency = state.currencies.find { it.code == currencyCode }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły: $currencyCode") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (currency == null) {
                Text("Nie znaleziono waluty.", modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Aktualny kurs", style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = String.format(Locale.US, "%.4f %s", currency.rate, state.baseCurrency),
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val dateString = remember(currency.lastUpdated) {
                                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                formatter.timeZone = TimeZone.getDefault()
                                formatter.format(Date(currency.lastUpdated))
                            }
                            Text(text = "Ostatnia aktualizacja: $dateString", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Źródło: ExchangeRate API", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Text(text = "Wykres kursu", style = MaterialTheme.typography.titleMedium)

                    val allPoints = remember(currency.rate, currencyCode, state.historicalRatesList) {
                        val historyList = state.historicalRatesList[currencyCode] ?: emptyList()
                        historyList + com.example.currencyexchangeapp.data.local.HistoricalRate(
                            timestamp = System.currentTimeMillis(),
                            rate = currency.rate
                        )
                    }
                    val chartEntryModel = remember(allPoints) {
                        val entries = if (allPoints.size > 1) {
                            allPoints.map { it.rate.toFloat() }
                        } else {
                            listOf(currency.rate.toFloat(), currency.rate.toFloat())
                        }
                        entryModelOf(*entries.toTypedArray())
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        Chart(
                            chart = lineChart(),
                            model = chartEntryModel,
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis(
                                valueFormatter = { value, _ ->
                                    val index = value.toInt()
                                    if (allPoints.size > 1 && index < allPoints.size) {
                                        val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                                        sdf.format(Date(allPoints[index].timestamp))
                                    } else if (allPoints.size <= 1) {
                                        if (value == 0f) "Poprzednio" else "Teraz"
                                    } else {
                                        ""
                                    }
                                }
                            ),
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}