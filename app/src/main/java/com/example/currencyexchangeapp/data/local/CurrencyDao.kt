package com.example.currencyexchangeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
// dao do definicji operacji SQL na tabeli z walutami
@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE isFavorite = 1")
    fun getFavoriteCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE code = :code")
    suspend fun getCurrencyById(code: String): CurrencyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<CurrencyEntity>)

    @Query("UPDATE currencies SET isFavorite = :isFavorite WHERE code = :code")
    suspend fun updateFavoriteStatus(code: String, isFavorite: Boolean)
}