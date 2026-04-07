package com.example.sportsgo.registers;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportsgo.DashboardActivity;
import com.example.sportsgo.R;
import com.example.sportsgo.firebase.FirebaseManager;

public class UserRegisterActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register_activity);

        etNombre   = findViewById(R.id.etNombre);
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrar());
    }

    private void registrar() {
        String nombre   = etNombre.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validaciones básicas
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseManager.getInstance().registerUser(
                nombre, email, password,
                unused -> {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    // va al dashboard
                    Intent intent = new Intent(UserRegisterActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                },
                e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );

    }
}