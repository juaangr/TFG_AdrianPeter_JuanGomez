package com.example.sportsgo;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //1. Inicializamos Realm
        Realm.init(this);

        //2. Configuracion de Realm para la bases de datos de la app
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("sportsgo_bd.realm") //Nombre del archivo de la base de datos
                .schemaVersion(1) //Control de versiones del esquema
                .deleteRealmIfMigrationNeeded() //Facilita el desarollo al actualizar
                .allowQueriesOnUiThread(true) //Optimizacion para consultas rapidas
                .allowWritesOnUiThread(true) //Permite transacciones inmediatas
                .compactOnLaunch() //Optimiza el espacio del disco al iniciar
                .build();

        Realm.setDefaultConfiguration(config);

        //Usamos Realm para la persistencia Offline-First, es decir en local
        //Firebase para la sincronizacion entre terminales, en la nube

    }
}
