package com.example.currencyexchangeapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchangeapp.data.local.CurrencyEntity
import com.example.currencyexchangeapp.data.local.PreferencesManager
import com.example.currencyexchangeapp.domain.repository.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first
// viewmodel zarządzający stanem ui i przetwarzający akcje użytkownika
data class CurrencyUiState(
    val isLoading: Boolean = false,
    val currencies: List<CurrencyEntity> = emptyList(),
    val error: String? = null,
    val baseCurrency: String = "PLN",
    val refreshInterval: Int = 60,
    val lastUpdateTimestamp: Long = 0L,
    val decimalPlaces: Int = 4,
    val historicalRatesList: Map<String, List<com.example.currencyexchangeapp.data.local.HistoricalRate>> = emptyMap()
)

class CurrencyViewModel(
    private val repository: CurrencyRepository,
    private val prefs: PreferencesManager
) : ViewModel() {
    private val _state = MutableStateFlow(CurrencyUiState())
    val state: StateFlow<CurrencyUiState> = _state.asStateFlow()

    private val _selectedCurrencyCode = MutableStateFlow<String?>(null)
    val selectedCurrencyCode = _selectedCurrencyCode.asStateFlow()

    fun selectCurrency(code: String?) {
        _selectedCurrencyCode.value = code
    }

    private suspend fun isNetworkAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    init {
        val savedBase = prefs.getBaseCurrency()
        val savedInterval = prefs.getRefreshIntervalMinutes()
        val savedTime = prefs.getLastUpdateTimestamp()
        val savedDecimalPlaces = prefs.getDecimalPlaces()
        val savedHistoryList = prefs.getFullHistoricalRates()
        _state.update { it.copy(
            baseCurrency = savedBase,
            refreshInterval = savedInterval,
            lastUpdateTimestamp = savedTime,
            decimalPlaces = savedDecimalPlaces,
            historicalRatesList = savedHistoryList
        ) }

        viewModelScope.launch {
            repository.getAllCurrencies().collect { currencyList ->
                _state.update { it.copy(currencies = currencyList) }
            }
        }
        checkAndRefreshDataIfNeeded()
    }

    fun refreshRates(simulateDeltas: Boolean = false) {
        viewModelScope.launch {
            val online = isNetworkAvailable()

            if (!online) {
                _state.update { it.copy(isLoading = false, error = "Brak połączenia z siecią") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val currentCurrencies = repository.getAllCurrencies().first()
                if (currentCurrencies.isNotEmpty()) {
                    val currentTime = System.currentTimeMillis()
                    val historyMap = currentCurrencies.associate { currency ->
                        val simulatedRate = if (simulateDeltas) {
                            currency.rate * (1.0 + (Math.random() - 0.5) * 0.05)
                        } else {
                            currency.rate
                        }
                        currency.code to com.example.currencyexchangeapp.data.local.HistoricalRate(currentTime, simulatedRate)
                    }
                    prefs.appendHistoricalRates(historyMap)
                }
                repository.refreshCurrencies(_state.value.baseCurrency)
                reloadHistory()
                val newTime = System.currentTimeMillis()
                prefs.setLastUpdateTimestamp(newTime)
                _state.update { it.copy(
                    isLoading = false,
                    lastUpdateTimestamp = newTime,
                    error = null
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Błąd serwera") }
            }
        }
    }

    fun toggleFavorite(code: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteStatus(code, isFavorite)
        }
    }

    fun updateBaseCurrency(newCurrency: String) {
        prefs.setBaseCurrency(newCurrency)
        _state.update { currentState ->
            currentState.copy(
                baseCurrency = newCurrency,
                isLoading = true
            )
        }
        refreshRates()
    }

    fun updateRefreshInterval(minutes: Int) {
        prefs.setRefreshIntervalMinutes(minutes)
        _state.update { it.copy(refreshInterval = minutes) }
    }

    fun saveApiKey(newKey: String) {
        prefs.setApiKey(newKey)
        refreshRates()
    }

    private fun checkAndRefreshDataIfNeeded() {
        viewModelScope.launch {
            val lastUpdate = prefs.getLastUpdateTimestamp()
            val intervalMillis = prefs.getRefreshIntervalMinutes() * 60 * 1000L
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdate >= intervalMillis) {
                refreshRates()
            }
        }
    }
    fun updateDecimalPlaces(places: Int) {
        prefs.setDecimalPlaces(places)
        _state.update { it.copy(decimalPlaces = places) }
    }
    fun scheduleBackgroundUpdates(context: Context, minutes: Int) {
        val interval = if (minutes < 15) 15L else minutes.toLong()
        val workRequest = PeriodicWorkRequestBuilder<com.example.currencyexchangeapp.data.work.CurrencyWorker>(
            interval, TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "CurrencyUpdateWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    // roznica od poprzedniego stanu
    fun getRateDifference(currencyCode: String, currentRate: Double): Double {
        val historyRate = state.value.historicalRatesList[currencyCode]?.lastOrNull()?.rate

        return if (historyRate != null) {
            currentRate - historyRate
        } else {
            0.0
        }
    }
    private fun reloadHistory() {
        val savedHistoryList = prefs.getFullHistoricalRates()
        _state.update { it.copy(historicalRatesList = savedHistoryList) }
    }
}