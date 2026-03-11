
plugins {
    alias(libs.plugins.android.application) apply false

}

buildscript{
    repositories{
        google()
        mavenCentral()
    }
    //Añadimos el plugin para habiliar la persistencia de datos y sincronización con Realm/MongoDB
    dependencies{
        classpath("io.realm:realm-gradle-plugin:10.16.1")
    }
}
