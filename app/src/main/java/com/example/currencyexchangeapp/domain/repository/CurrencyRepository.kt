package com.example.currencyexchangeapp.domain.repository

import com.example.currencyexchangeapp.data.local.CurrencyEntity
import kotlinx.coroutines.flow.Flow
// interf definiujący operacje dotyczące walut dla viewmodelu
interface CurrencyRepository {
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>
    fun getFavoriteCurrencies(): Flow<List<CurrencyEntity>>

    suspend fun refreshCurrencies(baseCurrency: String)
    suspend fun toggleFavoriteStatus(code: String, isFavorite: Boolean)
}