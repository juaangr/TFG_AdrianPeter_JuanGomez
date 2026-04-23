package com.example.sportsgo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sportsgo.logins.UserLoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inicializamos realm en la pantalla de carga
        io.realm.Realm.init(this);
        setContentView(R.layout.activity_splash);

        // Esperamos 3 segundos para que se vea el logo y el ProgressBar
        new Handler().postDelayed(() -> {
            // Despues del tiempo, saltamos al Login
            Intent intent = new Intent(SplashActivity.this, UserLoginActivity.class);
            startActivity(intent);
            finish(); // Cerramos el Splash para que no puedan volver atras
        }, 3000);
    }
}