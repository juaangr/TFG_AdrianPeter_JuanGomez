package com.example.miapp2trimes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class  MainActivity extends AppCompatActivity {
    private EditText etNombre, etEdad;
    private Button btn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        etNombre  = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        btn = findViewById(R.id.btnIniciar);

        //Recuperar preferencias al iniciar la app
        SharedPreferences prefs = getSharedPreferences("PrefeSportsGO", Context.MODE_PRIVATE);
        String nombreGuardado = prefs.getString("user_nombre","");
        etNombre.setText(nombreGuardado);

        btn.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String edadStr = etEdad.getText().toString().trim();

            if(nombre.isEmpty() || edadStr.isEmpty()){
                Toast.makeText(this, "Porfavor rellene todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            int edad  = Integer.parseInt(edadStr);

            if(edad >=18){
                DialogoAlerta.mostrar(this, new DialogoAlerta.OnResultadoDialogo() {
                    @Override
                    public void alAceptar() {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("user_nombre", nombre);
                        editor.putInt("user_edad", edad);
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, SegundoActivity.class);
                        intent.putExtra("key_nombre", nombre);
                        intent.putExtra("key_edad", edad);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "Bienvenido a nuestra pagina de deporte sportsGO", Toast.LENGTH_SHORT).show();

                    }
                });
            }else {
                Toast.makeText(this, "Debes de ser mayor de edad para acceder", Toast.LENGTH_SHORT).show();

            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}