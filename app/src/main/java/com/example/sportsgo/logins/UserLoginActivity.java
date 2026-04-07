package com.example.sportsgo.logins;

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

import com.example.sportsgo.DialogoAlerta;
import com.example.sportsgo.R;
import com.example.sportsgo.main.MainActivity;

/*
* ESTA CLASE SE DIVIDIRÁ EN 2, LA LÓGICA HABRÁ QUE CAMBIARLA Y DEMÁS
* */

public class UserLoginActivity extends AppCompatActivity {

    // inicialización de variables...
    private EditText etNombre, etPassw, etEmail;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_login_activity);

        // ...declaración de las mismas
        etNombre  = findViewById(R.id.etNombre);
        etPassw = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        // el botón de iniciar nos servirá para hacer un "log in" nos guardará el usr en la bbdd por ahora
        btn = findViewById(R.id.btnIniciar);

        // Recuperar preferencias al iniciar la app
        SharedPreferences prefs = getSharedPreferences("PrefeSportsGO", Context.MODE_PRIVATE);
        String nombreGuardado = prefs.getString("user_nombre","");
        etNombre.setText(nombreGuardado);

        // Handler del botón
        btn.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String passw = etPassw.getText().toString().trim();

            if(nombre.isEmpty() || email.isEmpty() || passw.isEmpty()){
                Toast.makeText(this, "Porfavor rellene todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int passwInt  = Integer.parseInt(passw);

            if(passwInt >=18){
                DialogoAlerta.mostrar(this, new DialogoAlerta.OnResultadoDialogo() {
                    @Override
                    public void alAceptar() {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("user_nombre", nombre);
                        editor.putInt("user_edad", passwInt);
                        editor.apply();

                        Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
                        intent.putExtra("key_nombre", nombre);
                        intent.putExtra("key_edad", passwInt);
                        startActivity(intent);
                        Toast.makeText(UserLoginActivity.this, "Bienvenido a nuestra pagina de deporte sportsGO", Toast.LENGTH_SHORT).show();

                    }
                });
            }else {
                Toast.makeText(this, "Debes de ser mayor de edad para acceder", Toast.LENGTH_SHORT).show();

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_login_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}
