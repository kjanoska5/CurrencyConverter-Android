package com.example.currencyexchangeapp

import android.app.Application
import com.example.currencyexchangeapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CurrencyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CurrencyApp)
            modules(appModule)
        }
    }
}