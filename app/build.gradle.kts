import java.util.Properties

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
        
        // Cargar la API KEY desde local.properties
        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }
        val geminiKey = properties.getProperty("gemini.api.key") ?: ""
        
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
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
    
    buildFeatures {
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Activamos la sincronización con MongoDB Atlas (Device Sync)
realm {
    isSyncEnabled = true
}

dependencies {
    // LIBRERÍA DE ADAPTADORES: Necesaria para el EjercicioAdapter y RealmBaseAdapter
    //implementation("io.realm:android-adapters:3.1.0")
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

    // Google Gemini AI
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Base de datos en tiempo real para el chat
    implementation("com.google.firebase:firebase-database")

    // Pruebas unitarias e instrumentales
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}