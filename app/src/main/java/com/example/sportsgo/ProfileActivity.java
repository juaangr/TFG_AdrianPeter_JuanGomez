package com.example.sportsgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import io.realm.Realm;

public class ProfileActivity extends AppCompatActivity {
    private TextInputEditText etNombre;
    private MaterialSwitch switchPrivacidad;
    private Button btnGuardar, btnLogout;
    private Realm realm;
    private Usuario usuarioActual;
    private String nombreSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        realm = Realm.getDefaultInstance();

        //Recuperamos la info del nombre de usuario que esta usando la app
        SharedPreferences prefs = getSharedPreferences("PrefeSportsGO", MODE_PRIVATE);
        nombreSesion = prefs.getString("user_nombre", "");

        initUI();
        cargarDatosUsuario();

        btnGuardar.setOnClickListener(v -> guardarCambios());

        btnLogout.setOnClickListener(v -> {
            //Limpiar la sesion y volver al inicio
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }

    private void guardarCambios() {
        String nuevoNombre = etNombre.getText().toString().trim();

        if (nuevoNombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacio", Toast.LENGTH_SHORT).show();
            return;
        }
        realm.executeTransaction(r -> {
            usuarioActual.setNombre(nuevoNombre);
            usuarioActual.setPermisoCompleto(switchPrivacidad.isChecked());
        });

        //Actualizamos las SharedPreferences por si cambio el nombre
        getSharedPreferences("PrefeSportsGO", MODE_PRIVATE).edit().putString("user_nombre", nuevoNombre).apply();

        Toast.makeText(this, "Perfil actualizado con exito", Toast.LENGTH_SHORT).show();
        finish(); //Volveremos al dashboard
    }

    private void cargarDatosUsuario() {
        //Buscamos el objeto usuario en el Realm que coincida con la sesion
        usuarioActual = realm.where(Usuario.class).equalTo("nombre", nombreSesion).findFirst();

        if (usuarioActual != null) {
            etNombre.setText(usuarioActual.getNombre());
            //Asumiendo que tienes un campo boolean llamado 'permisoCompleto' en tu clase Usuario
            switchPrivacidad.setChecked(usuarioActual.isPermisoCompleto());
        }
    }

    private void initUI() {
        etNombre = findViewById(R.id.etNombrePerfil);
        switchPrivacidad = findViewById(R.id.switchPrivacidad);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);
        btnLogout = findViewById(R.id.btnLogout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) realm.close();
    }
}