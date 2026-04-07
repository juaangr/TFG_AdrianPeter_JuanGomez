// Plugin o mejor dicho interruptor para activar las funciones de Realm/MongoDB
plugins {
    alias(libs.plugins.android.application)
    id("realm-android")                    // Activa Realm
    id("com.google.gms.google-services")
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
    // LIBRERIA PARA LA NUBE: FIREBASE
    //implementation("com.google.firebase:firebase-database:21.0.0")

    // Dependencias base del proyecto (Material Design 3 y AndroidX)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    //Import del Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.firebase.database)

    // Pruebas unitarias e instrumentales
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}