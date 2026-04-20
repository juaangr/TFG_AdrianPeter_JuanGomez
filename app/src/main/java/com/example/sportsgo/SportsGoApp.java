package com.example.sportsgo;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class SportsGoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmMigration migration = (realm, oldVersion, newVersion) -> {
            RealmSchema schema = realm.getSchema();
            if (oldVersion < 2) {
                RealmObjectSchema ejerciciosSchema = schema.get("Ejercicios");
                if (ejerciciosSchema != null) {
                    if (!ejerciciosSchema.hasField("categoria")) {
                        ejerciciosSchema.addField("categoria", String.class);
                    }
                    if (!ejerciciosSchema.hasField("grupoMuscular")) {
                        ejerciciosSchema.addField("grupoMuscular", String.class);
                    }
                    if (!ejerciciosSchema.hasField("plantilla")) {
                        ejerciciosSchema.addField("plantilla", boolean.class);
                    }
                }
                oldVersion = 2;
            }
        };

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("sportsgo_bd.realm")
                .schemaVersion(2)
                .migration(migration)
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .compactOnLaunch()
                .build();

        Realm.setDefaultConfiguration(config);
    }
}