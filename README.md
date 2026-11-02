# Currency Converter

A native Android application built with Kotlin and Jetpack Compose that allows users to track real-time currency exchange rates.

## Features
* Real-time Data: Fetches live exchange rates using the ExchangeRate REST API.
* Offline Mode: Caches API responses locally to ensure functionality without an active internet connection.
* Security: Safely stores sensitive configuration data, including API keys, using EncryptedSharedPreferences.
* Responsive UI: Adapts seamlessly to both phone and tablet layouts using Jetpack Compose.

## Tech Stack
* Language: Kotlin
* UI Toolkit: Jetpack Compose
* Architecture: MVVM (Model-View-ViewModel)
* Networking: Retrofit, JSON parsing
* Storage & Security: Room Database, EncryptedSharedPreferences

## Setup Instructions
1. Clone the repository.
2. Open the project in Android Studio.
3. Obtain a free API key from ExchangeRate-API.
4. Provide the API key via the application settings or secure local properties to enable network synchronization.
5. Build and run the application on an emulator or a physical device.