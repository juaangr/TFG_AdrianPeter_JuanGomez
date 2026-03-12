package com.example.miapp2trimes;

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
                .name("sportsgo_bd.realm")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .compactOnLaunch()
                .build();

        Realm.setDefaultConfiguration(config);


    }
}
