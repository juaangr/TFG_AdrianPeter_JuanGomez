package com.example.sportsgo.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportsgo.CaloriesActivity;
import com.example.sportsgo.DashboardActivity;
import com.example.sportsgo.ElegirLoginActivity;
import com.example.sportsgo.TrainerDashboardActivity;
import com.example.sportsgo.firebase.FirebaseManager;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ahora el contexto ya existe y getSharedPreferences funciona
        SharedPreferences prefs = getSharedPreferences("PrefeSportsGO", MODE_PRIVATE);
        boolean estaLogueado = prefs.getBoolean("logueado", false);

        FirebaseUser firebaseUser = FirebaseManager.getInstance().getUsuarioActual();
        if (firebaseUser != null) {
            resolverRutaFirebase(firebaseUser.getUid());
            return;
        }

        if (!estaLogueado) {
            startActivity(new Intent(this, ElegirLoginActivity.class));
        } else {
            String rol = prefs.getString("user_rol", "Pupilo");
            if ("Trainer".equalsIgnoreCase(rol)) {
                startActivity(new Intent(this, TrainerDashboardActivity.class));
            } else {
                startActivity(new Intent(this, DashboardActivity.class));
            }
        }

        finish();
    }

    private void resolverRutaFirebase(String uid) {
        FirebaseManager.getInstance().getRol(uid, rol -> {
            if ("trainers".equalsIgnoreCase(rol)) {
                startActivity(new Intent(this, TrainerDashboardActivity.class));
                finish();
                return;
            }

            if ("atletas".equalsIgnoreCase(rol)) {
                FirebaseManager.getInstance().getAtletaCalorias(uid, calorias -> {
                    if (calorias <= 0) {
                        startActivity(new Intent(this, CaloriesActivity.class));
                    } else {
                        startActivity(new Intent(this, DashboardActivity.class));
                    }
                    finish();
                }, e -> {
                    startActivity(new Intent(this, DashboardActivity.class));
                    finish();
                });
                return;
            }

            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }, e -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });
    }
}