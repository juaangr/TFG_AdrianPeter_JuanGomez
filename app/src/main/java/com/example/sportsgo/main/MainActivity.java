package com.example.sportsgo.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportsgo.DashboardActivity;
import com.example.sportsgo.ElegirLoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ahora el contexto ya existe y getSharedPreferences funciona
        SharedPreferences prefs = getSharedPreferences("PrefeSportsGO", MODE_PRIVATE);
        boolean estaLogueado = prefs.getBoolean("logueado", false);

        if (!estaLogueado) {
            startActivity(new Intent(this, ElegirLoginActivity.class));
        } else {
            startActivity(new Intent(this, DashboardActivity.class));
        }

        finish();
    }
}