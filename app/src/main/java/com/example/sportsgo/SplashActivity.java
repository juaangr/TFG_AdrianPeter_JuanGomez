package com.example.sportsgo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportsgo.logins.UserLoginActivity;

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable navigateToLogin = () -> {
        Intent intent = new Intent(SplashActivity.this, UserLoginActivity.class);
        startActivity(intent);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Esperamos 3 segundos para que se vea el logo y el ProgressBar
        handler.postDelayed(navigateToLogin, 4000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Evita callbacks pendientes si la Activity se cierra antes de tiempo
        handler.removeCallbacks(navigateToLogin);
    }
}