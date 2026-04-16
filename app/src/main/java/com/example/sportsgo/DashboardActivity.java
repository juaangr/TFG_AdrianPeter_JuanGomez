package com.example.sportsgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class DashboardActivity extends AppCompatActivity {
    private MaterialCardView cardWorkout, cardChat, cardCalorias, cardPerfil, cardTabata, cardIMC;
    private ImageView btnIAHeader, btnNavChat, btnNavPerfil, btnLogout;
    private TextView tvBienvenida;
    private String userRol, userNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        //Recuperamos la info del usuario logueado
        SharedPreferences prefs = getSharedPreferences("PrefeSportsGO", MODE_PRIVATE);
        userRol = prefs.getString("user_rol","Pupilo");
        userNombre = prefs.getString("user_nombre","Atleta");

        initUI();
        aplicarAnimaciones();

        // 1. Navegación de las Cards Principales
        cardWorkout.setOnClickListener(v -> {
            if(userRol.equals("Trainer")){
                startActivity(new Intent(this, TrainerDashboardActivity.class));
            }else{
                startActivity(new Intent(this, PupilWorkoutActivity.class));
            }
        });

        cardChat.setOnClickListener(v -> abrirChatHumano());
        cardPerfil.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        cardCalorias.setOnClickListener(v -> startActivity(new Intent(this, CaloriesActivity.class)));
        cardTabata.setOnClickListener(v -> startActivity(new Intent(this, TabataActivity.class)));
        cardIMC.setOnClickListener(v -> startActivity(new Intent(this, IMCActivity.class)));

        // 2. Navegación de botones específicos
        btnIAHeader.setOnClickListener(v -> startActivity(new Intent(this, GeminiChatActivity.class)));
        btnNavChat.setOnClickListener(v -> abrirChatHumano());
        btnNavPerfil.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnLogout.setOnClickListener(v -> finish());
    }

    private void abrirChatHumano() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("nombre_otro", userRol.equals("Trainer") ? "Selecciona un alumno" : "Entrenador");
        startActivity(intent);
    }

    private void aplicarAnimaciones() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        cardWorkout.startAnimation(anim);
        cardChat.startAnimation(anim);
        cardCalorias.startAnimation(anim);
        cardPerfil.startAnimation(anim);
        cardTabata.startAnimation(anim);
        cardIMC.startAnimation(anim);
    }

    private void initUI() {
        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvBienvenida.setText("Hola, "+userNombre);

        cardWorkout = findViewById(R.id.cardWorkout);
        cardChat = findViewById(R.id.cardChat);
        cardCalorias = findViewById(R.id.cardCalorias);
        cardPerfil = findViewById(R.id.cardPerfil);
        cardTabata = findViewById(R.id.cardTabata);
        cardIMC = findViewById(R.id.cardIMC);

        // Referencias correctas a los IDs del XML actual
        btnIAHeader = findViewById(R.id.btnIAHeader);
        btnNavChat = findViewById(R.id.btnNavChat);
        btnNavPerfil = findViewById(R.id.btnNavPerfil);
        btnLogout = findViewById(R.id.btnLogout);
    }
}
