package com.example.currencyexchangeapp.data.repository

import android.util.Log
import com.example.currencyexchangeapp.data.local.CurrencyDao
import com.example.currencyexchangeapp.data.local.CurrencyEntity
import com.example.currencyexchangeapp.data.local.PreferencesManager
import com.example.currencyexchangeapp.data.remote.CurrencyApiService
import com.example.currencyexchangeapp.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
// repo - pośrednik między remote a local do logiki synchronizacji
class CurrencyRepositoryImpl(
    private val apiService: CurrencyApiService,
    private val dao: CurrencyDao,
    private val prefs: PreferencesManager
) : CurrencyRepository {

    override fun getAllCurrencies(): Flow<List<CurrencyEntity>> = dao.getAllCurrencies()

    override fun getFavoriteCurrencies(): Flow<List<CurrencyEntity>> = dao.getFavoriteCurrencies()

    override suspend fun refreshCurrencies(baseCurrency: String) {
        try {
            val apiKey = prefs.getApiKey()
            if (apiKey.isNullOrEmpty()) {
                Log.e("Repo", "Brak klucza API. Dodaj go w ustawieniach.")
                return
            }

            val response = apiService.getLatestRates(apiKey, baseCurrency)

            val currentDbCurrencies = dao.getAllCurrencies().first()

            val entitiesToInsert = response.conversionRates.map { (code, rate) ->
                val existing = currentDbCurrencies.find { it.code == code }
                CurrencyEntity(
                    code = code,
                    rate = rate,
                    isFavorite = existing?.isFavorite ?: false,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            dao.insertCurrencies(entitiesToInsert)
            prefs.setLastUpdateTimestamp(System.currentTimeMillis())

        } catch (e: Exception) {
            Log.e("Repo", "Tryb offline lub błąd pobierania: ${e.message}")
        }
    }

    override suspend fun toggleFavoriteStatus(code: String, isFavorite: Boolean) {
        dao.updateFavoriteStatus(code, isFavorite)
    }
}