package com.example.currencyexchangeapp.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
// zarządza ustawieniami aplikacji i bezpiecznym przechowywaniem klucza API przez encrypted...

data class HistoricalRate(
    val timestamp: Long,
    val rate: Double
)
class PreferencesManager(context: Context) {

    private val standardPrefs: SharedPreferences = context.getSharedPreferences(
        "app_preferences",
        Context.MODE_PRIVATE
    )
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_api_preferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getBaseCurrency(): String {
        return standardPrefs.getString("base_currency", "PLN") ?: "PLN"
    }

    fun setBaseCurrency(currencyCode: String) {
        standardPrefs.edit().putString("base_currency", currencyCode).apply()
    }

    fun getRefreshIntervalMinutes(): Int {
        return standardPrefs.getInt("refresh_interval", 60)
    }

    fun setRefreshIntervalMinutes(minutes: Int) {
        standardPrefs.edit().putInt("refresh_interval", minutes).apply()
    }

    fun getLastUpdateTimestamp(): Long {
        return standardPrefs.getLong("last_update_timestamp", 0L)
    }

    fun setLastUpdateTimestamp(timestamp: Long) {
        standardPrefs.edit().putLong("last_update_timestamp", timestamp).apply()
    }

    fun getApiKey(): String? {
        return encryptedPrefs.getString("api_key", null)
    }

    fun setApiKey(apiKey: String) {
        encryptedPrefs.edit().putString("api_key", apiKey).apply()
    }

    fun setDecimalPlaces(places: Int) {
        standardPrefs.edit().putInt("decimal_places", places).apply()
    }

    fun getDecimalPlaces(): Int {
        return standardPrefs.getInt("decimal_places", 4)
    }
    //historia do kursow
    fun appendHistoricalRates(currentRates: Map<String, HistoricalRate>) {
        val existingJson = standardPrefs.getString("historical_rates_with_date", null)
        val type = object : TypeToken<MutableMap<String, MutableList<HistoricalRate>>>() {}.type

        val historyMap: MutableMap<String, MutableList<HistoricalRate>> = if (existingJson != null) {
            Gson().fromJson(existingJson, type) ?: mutableMapOf()
        } else {
            mutableMapOf()
        }

        currentRates.forEach { (code, record) ->
            val list = historyMap.getOrPut(code) { mutableListOf() }
            list.add(record)
        }

        val newJson = Gson().toJson(historyMap)
        standardPrefs.edit().putString("historical_rates_with_date", newJson).apply()
    }

    fun getFullHistoricalRates(): Map<String, List<HistoricalRate>> {
        val json = standardPrefs.getString("historical_rates_with_date", null) ?: return emptyMap()
        val type = object : TypeToken<Map<String, List<HistoricalRate>>>() {}.type
        return Gson().fromJson(json, type) ?: emptyMap()
    }
}