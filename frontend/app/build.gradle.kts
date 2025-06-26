plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.subastaapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.subastaapp"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}
dependencies {
    // Retrofit y GSON
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7") // Deja esta si ya te funciona bien, o actualiza a la última si no hay conflictos

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0") // Deja esta si ya te funciona bien

    // Coil para carga de imágenes
    implementation("io.coil-kt:coil-compose:2.6.0") // ¡Actualizado a 2.6.0!

    // Core KTX y Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // BOM de Compose (Gestor de versiones para Compose)
    implementation(platform(libs.androidx.compose.bom))
    // Componentes de UI de Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Material Design 3
    implementation(libs.androidx.material3)

    // ¡¡¡IMPORTANTE!!! AÑADIR ESTA LÍNEA PARA LOS ICONOS EXTENDIDOS
    implementation("androidx.compose.material:material-icons-extended:1.6.7") // O la versión que corresponda con tu compose.bom

    // Dependencias de testing
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
// Para pruebas de corrutinas
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("io.mockk:mockk:1.13.11")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}