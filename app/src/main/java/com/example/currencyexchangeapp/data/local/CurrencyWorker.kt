package com.example.currencyexchangeapp.data.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.currencyexchangeapp.data.local.PreferencesManager
import com.example.currencyexchangeapp.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CurrencyWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: CurrencyRepository by inject()
    private val prefs: PreferencesManager by inject()

    override suspend fun doWork(): Result {
        return try {
            Log.d("CurrencyWorker", "Rozpoczęto aktualizację w tle")

            // zapis ostatnich kursow do historii
            val currentCurrencies = repository.getAllCurrencies().first()
            if (currentCurrencies.isNotEmpty()) {
                val currentTime = System.currentTimeMillis() // obecny czas
                val historyMap = currentCurrencies.associate {
                    it.code to com.example.currencyexchangeapp.data.local.HistoricalRate(currentTime, it.rate)
                }
                prefs.appendHistoricalRates(historyMap)
            }

            // pobieranie nowych kurrsow
            repository.refreshCurrencies(prefs.getBaseCurrency())

            Log.d("CurrencyWorker", "Zakończono aktualizację")
            Result.success()
        } catch (e: Exception) {
            Log.e("CurrencyWorker", "Błąd pobierania w WorkManagerze: ${e.message}")
            Result.retry()
        }
    }
}