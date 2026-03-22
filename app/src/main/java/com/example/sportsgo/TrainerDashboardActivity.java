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

        realm.executeTransaction(r ->{
            r.delete(Usuario.class);

            Usuario u1 = new Usuario("Adrian atleta", "Pupilo");
            r.insertOrUpdate(u1);
            Usuario u2 = new Usuario("Juan Alumno", "Pupilo");
            r.insertOrUpdate(u2);
            Usuario u3 = new Usuario("Entrenador Pepe", "Trainer");
            r.insertOrUpdate(u3);
        });

        lvPupilos = findViewById(R.id.lvPupilos);

        //Logica para llenar las listas
        cargarListaPupilos();

        //Logica para cuando el trainer clique un pupilo pueda asignarle ejercicios y rutinas
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

        //Si la lista esta vacía, el adaptador no fallara, simplemente no mostrara nada
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
