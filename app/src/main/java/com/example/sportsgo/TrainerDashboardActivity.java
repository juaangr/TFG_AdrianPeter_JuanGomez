package com.example.sportsgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class TrainerDashboardActivity extends AppCompatActivity {
    private Realm realm;
    private ListView lvPupilos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_dashboard);

        realm = Realm.getDefaultInstance();

        // LÓGICA DE DATOS: Solo insertamos si la base de datos está VACÍA
        realm.executeTransaction(r -> {
            // COMENTAMOS EL DELETE: No queremos borrar los datos que ya existen
            // r.delete(Usuario.class);

            if (r.where(Usuario.class).count() == 0) {
                // Solo creamos estos usuarios de prueba la primera vez que se instala la app
                r.insertOrUpdate(new Usuario("Adrian atleta", "Pupilo"));
                r.insertOrUpdate(new Usuario("Juan Alumno", "Pupilo"));
                r.insertOrUpdate(new Usuario("Entrenador Pepe", "Trainer"));
                android.util.Log.d("REALM", "Base de datos vacía. Usuarios iniciales creados.");
            }
        });

        lvPupilos = findViewById(R.id.lvPupilos);
        cargarListaPupilos();

        lvPupilos.setOnItemClickListener((parent, view, position, id) -> {
            String nombreSeleccionado = (String) parent.getItemAtPosition(position);
            Intent intent = new Intent(TrainerDashboardActivity.this, TrainerAssignWorkoutActivity.class);
            intent.putExtra("nombre_pupilo", nombreSeleccionado);
            startActivity(intent);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(realm != null){
            realm.close();
        }
    }
}
