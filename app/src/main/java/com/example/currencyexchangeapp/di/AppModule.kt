package com.example.currencyexchangeapp.di

import androidx.room.Room
import com.example.currencyexchangeapp.data.local.CurrencyDatabase
import com.example.currencyexchangeapp.data.local.PreferencesManager
import com.example.currencyexchangeapp.data.remote.CurrencyApiService
import com.example.currencyexchangeapp.data.repository.CurrencyRepositoryImpl
import com.example.currencyexchangeapp.domain.repository.CurrencyRepository
import com.example.currencyexchangeapp.presentation.viewmodel.CurrencyViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.androidx.viewmodel.dsl.viewModel
// koin - definiuje jak tworzyć i dostarczać obiekty
val appModule = module {
    single { PreferencesManager(androidContext()) }

    single {
        Room.databaseBuilder(
            androidContext(),
            CurrencyDatabase::class.java,
            "currency_db"
        ).build()
    }
    single { get<CurrencyDatabase>().dao }

    single {
        Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }
    single<CurrencyRepository> { CurrencyRepositoryImpl(get(), get(), get())}

    viewModel { CurrencyViewModel(get(), get()) }
}