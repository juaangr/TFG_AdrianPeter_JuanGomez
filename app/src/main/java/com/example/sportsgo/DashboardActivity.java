package com.example.sportsgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class DashboardActivity extends AppCompatActivity {
    private MaterialCardView cardWorkout, cardChat, cardCalorias, cardPerfil;
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

        //Accedemos al sistema de rutinas de ejercicios
        cardWorkout.setOnClickListener(v -> {
            if(userRol.equals("Trainer")){
                //Si es entrenador, ve su lista de alumnos
                startActivity(new Intent(this, TrainerDashboardActivity.class));
            }else{
                //Si es pupilo, ve su rutina asignada
                startActivity(new Intent(this, PupilWorkoutActivity.class));
            }
        });

        //Accedemos al sistema de chat
        cardChat.setOnClickListener(v -> {
            //Navegacion al sistema de chateo en tiempo real
            Intent intent = new Intent(this, ChatActivity.class);

            //Si eres alumno, podrias pasar por defecto el nombre de tu entrenador
            intent.putExtra("nombre_otro",userRol.equals("Trainer") ? "Selecciona un alumno" : "Tu entrenador");
            startActivity(intent);
        });

        //Accedemos a la actividad del perfil para poder editarlo
        cardPerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        //Accedemos a la actividad del conteo de calorias para el usuario
        cardCalorias.setOnClickListener(v -> {
            //Navegamos a la actividad del conteo propio de calorias para el usuario
            Intent intent = new Intent(this, CaloriesActivity.class);
            startActivity(intent);
        });
    }

    private void aplicarAnimaciones() {
        // Cargamos la animacion fade_in_up que creamos anteriormente
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        cardWorkout.startAnimation(anim);
        cardChat.startAnimation(anim);
        cardCalorias.startAnimation(anim);
        cardPerfil.startAnimation(anim);
    }

    private void initUI() {
        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvBienvenida.setText("Hola, "+userNombre);

        cardWorkout = findViewById(R.id.cardWorkout);
        cardChat = findViewById(R.id.cardChat);
        cardCalorias = findViewById(R.id.cardCalorias);
        cardPerfil = findViewById(R.id.cardPerfil);
    }
}
