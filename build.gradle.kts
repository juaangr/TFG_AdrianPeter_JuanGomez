// TOP-LEVEL: Configuración global del motor de construcción para SportsGO

/**  El bloque buildscript prepara el "motor" de Realm antes de que empiece
 * la compilación de los módulos. Es vital para la arquitectura del proyecto.
 */
buildscript {
    repositories{
        google()
        mavenCentral()
    }

    dependencies {
        // Inyectamos el plugin de Realm/MongoDB Atlas.
        // Esto permite que nuestras clases Java se conviertan en objetos persistentes.
        classpath("io.realm:realm-gradle-plugin:10.19.0")
    }
}


/**
 * Bloque de plugins (DSL).
 * Aquí declaramos los plugins base, pero los activamos realmente en el archivo :app.
 */
plugins {
    // Plugin de Android Application gestionado mediante el Version Catalog (libs.versions.toml)
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
