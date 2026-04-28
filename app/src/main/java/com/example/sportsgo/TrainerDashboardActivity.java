package com.example.sportsgo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class TrainerDashboardActivity extends AppCompatActivity {
    private Realm realm;
    private ListView lvPupilos;
    private DatabaseReference mDatabase;
    private Button btnBanco;
    private Button btnAsignarRutina;
    private Button btnEstadisticas;
    private Button btnChatTrainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_dashboard);

        realm = Realm.getDefaultInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // LÓGICA DE DATOS: Solo insertamos si la base de datos está VACÍA
        realm.executeTransaction(r -> {
            // COMENTAMOS EL DELETE: No queremos borrar los datos que ya existen
            // r.delete(Usuario.class);

            if (r.where(Usuario.class).count() == 0) {
                // Solo creamos estos usuarios de prueba la primera vez que se instala la app
                Usuario u1 = new Usuario("Adrian Atleta", "Pupilo");
                Usuario u2 = new Usuario("Juan Alumno", "Pupilo");
                Usuario u3 = new Usuario("Entrenador Pepe", "Trainer");

                r.insertOrUpdate(u1);
                r.insertOrUpdate(u2);
                r.insertOrUpdate(u3);
                mDatabase.child("usuarios").child(u1.getId()).setValue(u1)
                        .addOnSuccessListener(aVoid -> Log.d("FIREBASE_TEST", "¡ÉXITO! Usuario subido a la nube."))
                        .addOnFailureListener(e -> Log.e("FIREBASE_TEST", "ERROR al subir: " + e.getMessage()));
                // --------------------------------------

                Log.d("REALM", "Base de datos vacía. Usuarios iniciales creados localmente.");
            }
        });

        lvPupilos = findViewById(R.id.lvPupilos);
        btnBanco = findViewById(R.id.btnBancoEjercicios);
        btnAsignarRutina = findViewById(R.id.btnAsignarRutina);
        btnEstadisticas = findViewById(R.id.btnEstadisticas);
        btnChatTrainer = findViewById(R.id.btnChatTrainer);
        cargarListaPupilos();

        btnBanco.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExerciseBankActivity.class);
            startActivity(intent);
        });

        btnAsignarRutina.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainerAssignWorkoutActivity.class);
            startActivity(intent);
        });

        btnEstadisticas.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.sportsgo.TrainerStatsActivity.class);
            startActivity(intent);
        });

        btnChatTrainer.setOnClickListener(v -> {
            mostrarSelectorPupilos();
        });

        lvPupilos.setOnItemClickListener((parent, view, position, id) -> {
            Usuario pupiloSeleccionado = (Usuario) parent.getItemAtPosition(position);

            if(pupiloSeleccionado != null){
                //Logica de privacidad
                if(!pupiloSeleccionado.isPermisoCompleto()){
                    //Si el alumno no permite el acceso al trainer le saldra el mensaje de abajo
                    Toast.makeText(this, "El alumno ha restringido el acceso a su perfil ", Toast.LENGTH_SHORT).show();
                }else{
                    //Si el alumno permite el acceso pues se procede normal
                    Intent intent = new Intent(this, TrainerAssignWorkoutActivity.class);
                    intent.putExtra("nombre_pupilo", pupiloSeleccionado.getNombre());
                    startActivity(intent);
                }
            }
        });
    }
    private void cargarListaPupilos() {
        RealmResults<Usuario> pupilos = realm.where(Usuario.class)
                .equalTo("rol", "Pupilo")
                .findAll();

        List<String> nombres = new ArrayList<>();

        for (Usuario p : pupilos) {
            System.out.println("Debug: Usuario encontrado: " + p.getNombre());
            //SEGURIDAD: Solo añadimos el nombre si no es nulo
            if (p.getNombre() != null && !p.getNombre().isEmpty()) {
                nombres.add(p.getNombre());
            } else {
                //Opcional: añadir un texto por defecto si el nombre fallo
                nombres.add("Usuario sin nombre (" + p.getId().toString() + ")");
            }
        }

        //Si la lista esta vacia, el adaptador no fallara, simplemente no mostrara nada
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, nombres);
        lvPupilos.setAdapter(adapter);
    }

    private void mostrarSelectorPupilos() {
        RealmResults<Usuario> pupilos = realm.where(Usuario.class)
                .equalTo("rol", "Pupilo")
                .findAll();

        if (pupilos == null || pupilos.isEmpty()) {
            Toast.makeText(this, "No hay pupilos para chatear", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> nombres = new ArrayList<>();
        for (Usuario p : pupilos) {
            if (p != null && p.getNombre() != null && !p.getNombre().isEmpty()) {
                nombres.add(p.getNombre());
            }
        }

        if (nombres.isEmpty()) {
            Toast.makeText(this, "No hay pupilos con nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] opciones = nombres.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("Selecciona un pupilo")
                .setItems(opciones, (dialog, which) -> {
                    String seleccionado = opciones[which];
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("nombre_otro", seleccionado);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(realm != null){
            realm.close();
        }
    }
}
