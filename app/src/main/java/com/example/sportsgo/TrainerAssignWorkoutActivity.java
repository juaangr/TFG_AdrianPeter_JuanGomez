package com.example.sportsgo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.bson.types.ObjectId;

import io.realm.Realm;

public class TrainerAssignWorkoutActivity extends AppCompatActivity {
    private TextView tvNombreAlumno;
    private TextInputEditText etNombre, etSeries, etReps, etPeso;
    private Spinner spinnerMusculo;
    private Button btnAsignar;

    private Realm realm;
    private String nombreAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_assign_workout);

        //Inicializamos la instancia de Realm para la persistencia de datos
        realm = Realm.getDefaultInstance();

        //Recuperamos el nombre del pupilo seleccionado en el dashboard previo
        nombreAlumno = getIntent().getStringExtra("nombre_pupilo");

        //Vinculacion de objetos de java con los IDs del layout XML
        tvNombreAlumno = findViewById(R.id.tvNombreAlumno);
        etNombre = findViewById(R.id.etNombreEj);
        etSeries = findViewById(R.id.etSeries);
        etReps = findViewById(R.id.etReps);
        etPeso = findViewById(R.id.etPesoEj);
        spinnerMusculo = findViewById(R.id.spinnerGrupoMuscular);
        btnAsignar = findViewById(R.id.btnAsignar);


        //Mostramos el nombre del alumno para confirmar a quien enviamos la rutina
        if (nombreAlumno != null) {
            tvNombreAlumno.setText("Asignando la rutina a: " + nombreAlumno);
        }

        //Configuracion del Spinner (Desplegable) con grupos musculares
        setupSpinner();

        //Listener del boton para ejecutar la logica de guardado
        btnAsignar.setOnClickListener(v -> guardarEjercicio());
    }

    private void setupSpinner() {
        String[] grupos = {"Pecho", "Espalda", "Hombro", "Biceps", "Triceps", "Pierna", "Core"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, grupos);

        spinnerMusculo.setAdapter(adapter);
    }


    private void guardarEjercicio() {
        //Validamos que los campos obligatorios tengan contenido
        String nombre = etNombre.getText().toString();
        String peso = etPeso.getText().toString();
        String series = etSeries.getText().toString();
        String reps = etReps.getText().toString();

        if (nombre.isEmpty() || peso.isEmpty() || series.isEmpty() || reps.isEmpty()) {
            Toast.makeText(this, "Porfavor. rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        //Realizaremos una transaccion de escritura en Realm, el cambio se sincronizara con Atlas
        // y aparecera en movil en tiempo real
        try {
            realm.executeTransaction(r -> {
                Ejercicios nuevoEj = r.createObject(Ejercicios.class, new ObjectId());
                nuevoEj.setNombre(nombre);
                nuevoEj.setPeso(peso);
                nuevoEj.setSeries(Integer.parseInt(series));
                nuevoEj.setRepeticiones(Integer.parseInt(reps));

                //Usamos el campo descripcion para guardar el grupo muscular seleccionado
                nuevoEj.setDescripcion(spinnerMusculo.getSelectedItem().toString());

                //Vinculamos el ejercicio al nombre del pupilo
                nuevoEj.setNombrePupilo(nombreAlumno);

                //Por defecto el ejercicio no esta completado (el alumno lo marcara cuando lo este)
                nuevoEj.setCompletado(false);
            });

            //Envio a la nube (Firebase)
            com.google.firebase.database.DatabaseReference mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("entrenamientos");

            //Preparamos el mapa de datos (clave-valor) para subirlos a la nube
            java.util.Map<String, Object> ejercicioNube = new java.util.HashMap<>();
            ejercicioNube.put("nombre",nombre);
            ejercicioNube.put("peso", peso);
            ejercicioNube.put("series", Integer.parseInt(series));
            ejercicioNube.put("repeticiones", Integer.parseInt(reps));
            ejercicioNube.put("nombrePupilo", nombreAlumno);
            ejercicioNube.put("completado", false);
            ejercicioNube.put("descripcion", spinnerMusculo.getSelectedItem().toString());

            if (nombreAlumno != null) {
                mDatabase.child(nombreAlumno).push().setValue(ejercicioNube)
                        .addOnSuccessListener(aVoid -> android.util.Log.d("FIREBASE", "Sincronizado con éxito"))
                        .addOnFailureListener(e -> android.util.Log.e("FIREBASE", "Error al subir", e));
            }

            //Toast de confirmacion de envio de los ejercicios o en caso de error su mensaje correspondiente

            Toast.makeText(this, "Rutina enviada a: " + nombreAlumno, Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Las series y repeticiones deben ser numeros", Toast.LENGTH_SHORT).show();
        }
    }
        @Override
        protected void onDestroy () {
            super.onDestroy();

            //Cerramos la instancia de Realm para evitar fugas de memoria
            if (realm != null) {
                realm.close();
            }
        }
    }

