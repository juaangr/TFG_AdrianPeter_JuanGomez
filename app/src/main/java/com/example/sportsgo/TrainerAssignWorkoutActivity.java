package com.example.sportsgo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class TrainerAssignWorkoutActivity extends AppCompatActivity {
    private TextView tvNombreAlumno;
    private TextInputEditText etNombre, etSeries, etReps, etPeso;
    private Spinner spinnerMusculo, spinnerCategoria;
    private CheckBox cbGuardarPlantilla;
    private Button btnAsignar, btnAbrirBanco;

    private Realm realm;
    private String nombreAlumno;

    private final ActivityResultLauncher<android.content.Intent> launcherBanco = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    return;
                }

                String templateId = result.getData().getStringExtra(ExerciseBankActivity.EXTRA_TEMPLATE_ID);
                if (templateId == null || templateId.trim().isEmpty()) {
                    return;
                }

                cargarPlantillaEnFormulario(templateId);
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_assign_workout);

        try {
            realm = Realm.getDefaultInstance();
        } catch (Throwable t) {
            Toast.makeText(this, "Realm no disponible en este arranque", Toast.LENGTH_SHORT).show();
        }

        //Recuperamos el nombre del pupilo seleccionado en el dashboard previo
        nombreAlumno = getIntent().getStringExtra("nombre_pupilo");
        if (TextUtils.isEmpty(nombreAlumno)) {
            // Permite pruebas directas de esta pantalla sin depender de otra Activity.
            nombreAlumno = "Pupilo de prueba";
        }

        //Vinculacion de objetos de java con los IDs del layout XML
        tvNombreAlumno = findViewById(R.id.tvNombreAlumno);
        etNombre = findViewById(R.id.etNombreEj);
        etSeries = findViewById(R.id.etSeries);
        etReps = findViewById(R.id.etReps);
        etPeso = findViewById(R.id.etPesoEj);
        spinnerMusculo = findViewById(R.id.spinnerGrupoMuscular);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        cbGuardarPlantilla = findViewById(R.id.cbGuardarPlantilla);
        btnAbrirBanco = findViewById(R.id.btnAbrirBanco);
        btnAsignar = findViewById(R.id.btnAsignar);


        //Mostramos el nombre del alumno para confirmar a quien enviamos la rutina
        if (!TextUtils.isEmpty(nombreAlumno)) {
            tvNombreAlumno.setText("Asignando la rutina a: " + nombreAlumno);
        }

        //Configuracion del Spinner (Desplegable) con grupos musculares
        setupSpinners();

        btnAbrirBanco.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, ExerciseBankActivity.class);
            launcherBanco.launch(intent);
        });

        //Listener del boton para ejecutar la logica de guardado
        btnAsignar.setOnClickListener(v -> guardarEjercicio());
    }

    private void setupSpinners() {
        String[] grupos = {"Pecho", "Espalda", "Hombro", "Biceps", "Triceps", "Pierna", "Core"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner_selected, grupos);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerMusculo.setAdapter(adapter);

        String[] categorias = {"Fuerza", "Cardio", "Flexibilidad"};
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this, R.layout.item_spinner_selected, categorias);
        adapterCategorias.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerCategoria.setAdapter(adapterCategorias);
    }

    private void cargarPlantillaEnFormulario(String templateId) {
        if (realm == null) {
            Toast.makeText(this, "Plantillas no disponibles", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Ejercicios plantilla = realm.where(Ejercicios.class)
                    .equalTo("id", new ObjectId(templateId))
                    .equalTo("plantilla", true)
                    .findFirst();

            if (plantilla == null) {
                Toast.makeText(this, "No se encontro la plantilla seleccionada", Toast.LENGTH_SHORT).show();
                return;
            }

            etNombre.setText(plantilla.getNombre());
            etSeries.setText(String.valueOf(plantilla.getSeries()));
            etReps.setText(String.valueOf(plantilla.getRepeticiones()));
            etPeso.setText(plantilla.getPeso());
            seleccionarSpinnerPorTexto(spinnerMusculo, plantilla.getGrupoMuscular());
            seleccionarSpinnerPorTexto(spinnerCategoria, plantilla.getCategoria());
            Toast.makeText(this, "Plantilla cargada. Ajusta peso/series si lo necesitas", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, "La plantilla recibida es invalida", Toast.LENGTH_SHORT).show();
        }
    }

    private void seleccionarSpinnerPorTexto(Spinner spinner, String value) {
        if (value == null) {
            return;
        }
        for (int i = 0; i < spinner.getCount(); i++) {
            Object item = spinner.getItemAtPosition(i);
            if (item != null && value.equalsIgnoreCase(item.toString())) {
                spinner.setSelection(i);
                return;
            }
        }
    }


    private void guardarEjercicio() {
        //Validamos que los campos obligatorios tengan contenido
        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String peso = etPeso.getText() != null ? etPeso.getText().toString().trim() : "";
        String series = etSeries.getText() != null ? etSeries.getText().toString().trim() : "";
        String reps = etReps.getText() != null ? etReps.getText().toString().trim() : "";

        if (nombre.isEmpty() || peso.isEmpty() || series.isEmpty() || reps.isEmpty()) {
            Toast.makeText(this, "Porfavor. rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        //Realizaremos una transaccion de escritura en Realm, el cambio se sincronizara con Atlas
        // y aparecera en movil en tiempo real
        try {
            if (realm == null) {
                Toast.makeText(this, "Realm no inicializado", Toast.LENGTH_SHORT).show();
                return;
            }
            Object grupoSeleccionado = spinnerMusculo.getSelectedItem();
            Object categoriaSeleccionada = spinnerCategoria.getSelectedItem();
            if (grupoSeleccionado == null || categoriaSeleccionada == null) {
                Toast.makeText(this, "Selecciona grupo muscular y categoria", Toast.LENGTH_SHORT).show();
                return;
            }

            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            String grupoMuscular = grupoSeleccionado.toString();
            String categoria = categoriaSeleccionada.toString();

            realm.executeTransaction(r -> {
                Ejercicios nuevoEj = r.createObject(Ejercicios.class, new ObjectId());
                nuevoEj.setNombre(nombre);
                nuevoEj.setPeso(peso);
                nuevoEj.setSeries(Integer.parseInt(series));
                nuevoEj.setRepeticiones(Integer.parseInt(reps));
                nuevoEj.setCategoria(categoria);
                nuevoEj.setGrupoMuscular(grupoMuscular);
                nuevoEj.setPlantilla(false);
                nuevoEj.setFechaAsignacion(fechaHoy);

                // Mantenemos la descripcion para no romper compatibilidad con datos antiguos.
                nuevoEj.setDescripcion(grupoMuscular);

                //Vinculamos el ejercicio al nombre del pupilo
                nuevoEj.setNombrePupilo(nombreAlumno);

                //Por defecto el ejercicio no esta completado (el alumno lo marcara cuando lo este)
                nuevoEj.setCompletado(false);

                if (cbGuardarPlantilla.isChecked()) {
                    Ejercicios plantilla = r.createObject(Ejercicios.class, new ObjectId());
                    plantilla.setNombre(nombre);
                    plantilla.setPeso(peso);
                    plantilla.setSeries(Integer.parseInt(series));
                    plantilla.setRepeticiones(Integer.parseInt(reps));
                    plantilla.setCategoria(categoria);
                    plantilla.setGrupoMuscular(grupoMuscular);
                    plantilla.setDescripcion("Plantilla del entrenador");
                    plantilla.setNombrePupilo("");
                    plantilla.setCompletado(false);
                    plantilla.setPlantilla(true);
                    plantilla.setImage(R.drawable.press);
                    plantilla.setFechaAsignacion("");
                }
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
            ejercicioNube.put("descripcion", grupoMuscular);
            ejercicioNube.put("categoria", categoria);
            ejercicioNube.put("grupoMuscular", grupoMuscular);
            ejercicioNube.put("plantilla", false);
            ejercicioNube.put("fechaAsignacion", fechaHoy);

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
        } catch (Throwable t) {
            Log.e("TrainerAssign", "Error al guardar ejercicio", t);
            Toast.makeText(this, "No se pudo guardar el ejercicio. Reintenta.", Toast.LENGTH_LONG).show();
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

