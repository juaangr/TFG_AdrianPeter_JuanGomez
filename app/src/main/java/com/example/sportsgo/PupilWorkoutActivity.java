package com.example.sportsgo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

//ESTA CLASE ES PARA LA VISTA DEL PUPILO DE LOS EJERCICIOS QUE LE ASIGNO SU ENTRENADOR
public class PupilWorkoutActivity extends AppCompatActivity {
    private Button button, btnGemini;
    private Button btnAnteriorEjercicio, btnSiguienteEjercicio;
    private TextView tvNavegacionEj;
    private ListView listView;
    private Realm realm;
    private EjercicioAdapter adapter;
    private RealmResults<Ejercicios> resultados;
    private int indiceActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pupil_workout);

        //Inicializamos realm
        realm = Realm.getDefaultInstance();

        button = findViewById(R.id.btnFinalizar);
        btnGemini = findViewById(R.id.btnGeminiChat);
        listView = findViewById(R.id.lvEjercicios);
        btnAnteriorEjercicio = findViewById(R.id.btnAnteriorEjercicio);
        btnSiguienteEjercicio = findViewById(R.id.btnSiguienteEjercicio);
        tvNavegacionEj = findViewById(R.id.tvNavegacionEj);

        // Listener para abrir el chat con Gemini
        btnGemini.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, GeminiChatActivity.class);
            startActivity(intent);
        });

        //Obtener el nombre del pupilo logueado (desde SharedPreference)
        String pupiloActual = getSharedPreferences("PrefeSportsGO", MODE_PRIVATE)
                .getString("user_nombre", "Adrian");

        // Filtramos por fecha para navegar solo entre ejercicios del mismo dia
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //Consulta Realm: ejercicios de este pupilo, no-plantilla y del dia actual
        resultados = realm.where(Ejercicios.class)
                .equalTo("nombrePupilo", pupiloActual)
                .equalTo("plantilla", false)
                .equalTo("fechaAsignacion", fechaHoy)
                .findAll();

        //Configurar el adaptador con los datos de la BD
        adapter = new EjercicioAdapter(this, resultados);
        listView.setAdapter(adapter);

        // Permite ajustar la navegacion cuando el usuario toca manualmente un ejercicio
        listView.setOnItemClickListener((parent, view, position, id) -> {
            indiceActual = position;
            actualizarEstadoNavegacion();
        });

        btnAnteriorEjercicio.setOnClickListener(v -> {
            if (indiceActual > 0) {
                indiceActual--;
                actualizarEstadoNavegacion();
            }
        });

        btnSiguienteEjercicio.setOnClickListener(v -> {
            if (indiceActual < adapter.getCount() - 1) {
                indiceActual++;
                actualizarEstadoNavegacion();
            }
        });

        // Escuchamos cambios en Realm para mantener la navegacion coherente
        resultados.addChangeListener(ejercicios -> {
            if (indiceActual >= ejercicios.size()) {
                indiceActual = Math.max(0, ejercicios.size() - 1);
            }
            actualizarEstadoNavegacion();
        });

        configurarSincronizacionNube(pupiloActual);
        actualizarEstadoNavegacion();

        //Listener para que el trainer vea el progreso marcando un checkbox que se sincroniza en tiempo real
        button.setOnClickListener(view -> {
            Toast.makeText(this, "Entrenamiento finalizado y enviado al Coach", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void actualizarEstadoNavegacion() {
        int total = adapter != null ? adapter.getCount() : 0;

        if (total == 0) {
            btnAnteriorEjercicio.setEnabled(false);
            btnSiguienteEjercicio.setEnabled(false);
            tvNavegacionEj.setText("Sin ejercicios para hoy");
            return;
        }

        if (indiceActual < 0) {
            indiceActual = 0;
        } else if (indiceActual >= total) {
            indiceActual = total - 1;
        }

        btnAnteriorEjercicio.setEnabled(indiceActual > 0);
        btnSiguienteEjercicio.setEnabled(indiceActual < total - 1);
        tvNavegacionEj.setText((indiceActual + 1) + " / " + total);
        listView.smoothScrollToPosition(indiceActual);
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
        if (resultados != null) {
            resultados.removeAllChangeListeners();
        }
        if (realm != null) {
            realm.close();
        }
    }
}

