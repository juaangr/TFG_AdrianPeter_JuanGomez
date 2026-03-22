package com.example.sportsgo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;
import io.realm.RealmResults;

//ESTA CLASE ES PARA LA VISTA DEL PUPILO DE LOS EJERCICIOS QUE LE ASIGNO SU ENTRENADOR
public class PupilWorkoutActivity extends AppCompatActivity {
   private Button button;
   private ListView listView;
   private Realm realm;
   private EjercicioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tercer_activity);

    //Inicializamos realm
    realm = Realm.getDefaultInstance();

    button = findViewById(R.id.btnFinalizar);
    listView = findViewById(R.id.lvEjercicios);

    //Obtener el nombre del pupilo logueado (desde SharedPreference)
    String pupiloActual = getSharedPreferences("PrefeSportsGO", MODE_PRIVATE).getString("user_nombre","Adrian");

    //Consulta Realm: Obtener ejercicios asignados a este pupilo, se ira actualizando
    // mediante el trainer vaya asignando ejercicios y se vean reflejados en tiempo real
    RealmResults<Ejercicios> resultados = realm.where(Ejercicios.class).equalTo("nombrePupilo",pupiloActual).findAll();

    //Configurar el adaptador con los datos de la BD
    adapter = new EjercicioAdapter(this, resultados);
    listView.setAdapter(adapter);

    //Listener para que el trainer vea el progreso marcando un checkbox que se sincroniza en tiempo real
    button.setOnClickListener(view -> {
        Toast.makeText(this,"Entrenamiento finalizado y enviado al Coach", Toast.LENGTH_SHORT).show();
        finish();
    });
   }
   @Override
    protected void onDestroy() {
        super.onDestroy();
        if(realm != null){
            realm.close();
        }
    }
}

