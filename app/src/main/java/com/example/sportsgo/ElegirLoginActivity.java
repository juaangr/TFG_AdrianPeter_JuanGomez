package com.example.sportsgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportsgo.registers.AdminRegisterActivity;
import com.example.sportsgo.registers.TrainerRegisterActivity;
import com.example.sportsgo.registers.UserRegisterActivity;

public class ElegirLoginActivity extends AppCompatActivity {

    // inicialización de variables...
    private Button btnAdmin, btnUser, btnTrainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.elegir_login_activity);

        // ...declaración de las mismas
        btnAdmin = findViewById(R.id.btnAdmin);
        btnUser = findViewById(R.id.btnUser);
        btnTrainer = findViewById(R.id.btnTrainer);


        // listeners de los eventos on click por botón (abre activities)
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // abre el login de admin
                Intent intent = new Intent(ElegirLoginActivity.this, AdminRegisterActivity.class);
                startActivity(intent);
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // abre el login de User
                Intent intent = new Intent(ElegirLoginActivity.this, UserRegisterActivity.class);
                startActivity(intent);
            }
        });

        btnTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // abre el login de Trainer
                Intent intent = new Intent(ElegirLoginActivity.this, TrainerRegisterActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.elegir_login_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
