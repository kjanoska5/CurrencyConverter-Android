package com.example.currencyexchangeapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
// def endpointów API dla retrofit do komunikacji HTTP z serwerem exchangeRate
interface CurrencyApiService {

    @GET("v6/{apiKey}/latest/{baseCurrency}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("baseCurrency") baseCurrency: String
    ): ExchangeRateResponse
}