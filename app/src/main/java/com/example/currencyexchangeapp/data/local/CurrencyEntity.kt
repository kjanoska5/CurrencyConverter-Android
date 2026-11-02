package com.example.currencyexchangeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey
    val code: String,
    val rate: Double,
    val previousRate: Double? = null,
    val isFavorite: Boolean = false,
    val lastUpdated: Long
)