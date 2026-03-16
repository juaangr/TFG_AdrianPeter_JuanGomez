package com.example.sportsgo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportsgo.R;

/*
* activity sin vista para manejar y redirigir a otras vistas
* */
public class  MainActivity extends AppCompatActivity {

    // recuperamos preferencias y si es la primera vez que se abre la app (que es lo mismo que no estar logeado)
    SharedPreferences prefs = getSharedPreferences("PrefeSportsGO", MODE_PRIVATE);
    boolean estaLogueado = prefs.getBoolean("logueado", false);
    boolean noLogeado = prefs.getBoolean("noLogueado", true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // pagina para elegir logearse como admin, user (pupilo) o trainer
        if(noLogeado) startActivity(new Intent(this, ElegirLoginActivity.class));
        // lo que podría ser una especie de tl rollo tw o instagram... esto no se puede descomentar a menos que tengamos la pantalla principal hecha
        // else if (estaLogueado) startActivity(new Intent(this, ));

        finish();
    }
}