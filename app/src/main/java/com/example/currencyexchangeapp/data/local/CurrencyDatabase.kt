package com.example.currencyexchangeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
// baza danych room z lok kopią kursów walut i statusu ulubionych.
@Database(
    entities = [CurrencyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract val dao: CurrencyDao
}