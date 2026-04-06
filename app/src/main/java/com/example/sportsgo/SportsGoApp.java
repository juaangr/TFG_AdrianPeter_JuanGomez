package com.example.sportsgo;

import android.app.Application;
import io.realm.Realm;

public class SportsGoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Esto se ejecuta UNA VEZ cuando se abre la app,
        // antes que cualquier pantalla.
        Realm.init(this);
    }
}