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

public class MainActivity extends AppCompatActivity {
    private EditText etNombre, etEdad;
    private Button btn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Referencias de la interfaz
        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        btn = findViewById(R.id.btnIniciar);

        // Recuperar preferencias al iniciar la app (las "cookies")
        SharedPreferences prefs = getSharedPreferences("PrefeSportsGO", Context.MODE_PRIVATE);
        String nombreGuardado = prefs.getString("user_nombre", "");
        etNombre.setText(nombreGuardado);

        btn.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String edadStr = etEdad.getText().toString().trim();

            // 1. Verificación de campos vacíos
            if (nombre.isEmpty() || edadStr.isEmpty()) {
                Toast.makeText(this, "Por favor rellene todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // 2. Convertir edad a número
                int edad = Integer.parseInt(edadStr);

                if (edad >= 18) {
                    // 3. Mostrar diálogo de alerta (tus "cookies")
                    DialogoAlerta.mostrar(this, new DialogoAlerta.OnResultadoDialogo() {
                        @Override
                        public void alAceptar() {
                            // Si acepta, guardamos los datos
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("user_nombre", nombre);
                            editor.putInt("user_edad", edad);
                            editor.apply();

                            // 4. SALTO AL SEGUNDO ACTIVITY
                            Intent intent = new Intent(MainActivity.this, SegundoActivityPupilo.class);
                            intent.putExtra("key_nombre", nombre);
                            intent.putExtra("key_edad", edad);
                            startActivity(intent);

                            Toast.makeText(MainActivity.this, "Bienvenido a SportsGO", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Debes de ser mayor de edad para acceder", Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                // Esto evita que la app se cierre si la edad no es un número
                Toast.makeText(this, "Introduce una edad válida", Toast.LENGTH_SHORT).show();
            }
        });

        // Configuración de los márgenes del sistema (evita que el diseño se corte)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}