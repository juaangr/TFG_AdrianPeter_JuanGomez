// Plugin o mejor dicho interruptor para activar las funciones de Realm/MongoDB
plugins {
    alias(libs.plugins.android.application)
    id("realm-android") // Activa la generación de código de Realm
}

android {
    namespace = "com.example.sportsgo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sportsgo"
        minSdk = 27
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Activamos la sincronización con MongoDB Atlas (Device Sync)
// Fundamental para la comunicación Trainer-Alumno en tiempo real
realm {
    isSyncEnabled = true
}

dependencies {
    // LIBRERÍA DE ADAPTADORES: Necesaria para el EjercicioAdapter y RealmBaseAdapter
    implementation("io.realm:android-adapters:3.1.0")

    // Dependencias base del proyecto (Material Design 3 y AndroidX)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Pruebas unitarias e instrumentales
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}