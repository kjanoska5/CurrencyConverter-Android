package com.example.currencyexchangeapp.data.remote

import com.google.gson.annotations.SerializedName
// dto mapujący odpowiedź JSON z serwera na obiekt kotlin
data class ExchangeRateResponse(
    @SerializedName("result")
    val result: String,

    @SerializedName("time_last_update_unix")
    val timeLastUpdateUnix: Long,

    @SerializedName("base_code")
    val baseCode: String,

    @SerializedName("conversion_rates")
    val conversionRates: Map<String, Double>
)