package com.example.sportsgo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;
import io.realm.RealmResults;

//ESTA CLASE ES PARA LA VISTA DEL PUPILO DE LOS EJERCICIOS QUE LE ASIGNO SU ENTRENADOR
public class PupilWorkoutActivity extends AppCompatActivity {
   private Button button, btnGemini;
   private ListView listView;
   private Realm realm;
   private EjercicioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pupil_workout);

    //Inicializamos realm
    realm = Realm.getDefaultInstance();

    button = findViewById(R.id.btnFinalizar);
    btnGemini = findViewById(R.id.btnGeminiChat);
    listView = findViewById(R.id.lvEjercicios);

    // Listener para abrir el chat con Gemini
    btnGemini.setOnClickListener(v -> {
        android.content.Intent intent = new android.content.Intent(this, GeminiChatActivity.class);
        startActivity(intent);
    });

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
    private void configurarSincronizacionNube(String nombreUsuario) {
        // Referencia a la "carpeta" de este pupilo en la nube de Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("entrenamientos").child(nombreUsuario);

        // Añadimos un listener que detecta cuando el Trainer añade algo nuevo
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                // Cuando el Trainer envía un ejercicio, Firebase nos avisa AQUÍ
                // Convertimos el dato de la nube a nuestra clase Ejercicios.java
                Ejercicios ejercicioNube = snapshot.getValue(Ejercicios.class);

                if (ejercicioNube != null) {
                    // Abrimos una transacción para guardar el ejercicio en el Realm local del PC/Movil
                    // Usamos copyToRealmOrUpdate para evitar duplicados si el ID coincide
                    Realm realmLocal = Realm.getDefaultInstance();
                    realmLocal.executeTransaction(r -> r.copyToRealmOrUpdate(ejercicioNube));
                    realmLocal.close();

                    // Al actualizar Realm, la lista (ListView) se refrescara sola automaticamente
                }
            }

            @Override public void onChildChanged(DataSnapshot s, String p) {}
            @Override public void onChildRemoved(DataSnapshot s) {}
            @Override public void onChildMoved(DataSnapshot s, String p) {}
            @Override public void onCancelled(DatabaseError e) {
                android.util.Log.e("FIREBASE", "Error de conexión: " + e.getMessage());
            }
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

