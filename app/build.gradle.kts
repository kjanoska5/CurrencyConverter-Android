plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.currencyexchangeapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.currencyexchangeapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation("androidx.compose.material3:material3:1.2.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // nawigacja
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // retrofit - siec
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    //room - baza lokalna
    val roomVersion = "2.7.0"
    implementation("androidx.room:room-runtime:${roomVersion}")
    implementation("androidx.room:room-ktx:${roomVersion}")
    ksp("androidx.room:room-compiler:$roomVersion")
    // szyfrowanie
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")

    // wykresy -vico
    implementation("com.patrykandpatrick.vico:compose:1.14.0")
    implementation("com.patrykandpatrick.vico:compose-m3:1.14.0")

    //koin
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")

    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    //workmanager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
}