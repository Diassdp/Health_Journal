plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.healthjournal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.healthjournal"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core libraries
    implementation(libs.androidx.core.ktx) // Kotlin extensions for core Android functionality
    implementation(libs.androidx.appcompat) // AppCompat library for backward compatibility
    implementation(libs.material) // Material Design components
    implementation(libs.androidx.activity) // Activity library for managing the activity lifecycle
    implementation(libs.androidx.constraintlayout) // ConstraintLayout for flexible layouts
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Firebase libraries
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")

    // Splash screen
    implementation("androidx.core:core-splashscreen:1.0.0") // For splash screen support

    // DataStore preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0") // For storing key-value pairs

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2") // Coroutines for asynchronous programming
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2") // Coroutines for Android

    // Circular image views
    implementation("de.hdodenhof:circleimageview:3.1.0") // For circular image views
    implementation ("com.google.android.material:material:1.7.0")
}