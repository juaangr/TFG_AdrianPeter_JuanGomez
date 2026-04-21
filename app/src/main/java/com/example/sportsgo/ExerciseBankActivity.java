package com.example.sportsgo;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ExerciseBankActivity extends AppCompatActivity {

    public static final String EXTRA_TEMPLATE_ID = "template_id";

    private static final String[] CATEGORIAS = {"Todas", "Fuerza", "Cardio", "Flexibilidad"};
    private static final String[] GRUPOS = {"Todos", "Pecho", "Espalda", "Hombro", "Biceps", "Triceps", "Pierna", "Core"};

    private static final String[] IMAGE_LABELS = {
            "Por defecto",
            "Press",
            "Biceps",
            "Hombro",
            "Pierna",
            "Gym"
    };

    private static final int[] IMAGE_RESOURCES = {
            R.mipmap.ic_launcher,
            R.drawable.press,
            R.drawable.biceps,
            R.drawable.hombro,
            R.drawable.pierna,
            R.drawable.gym_foto
    };

    private Realm realm;
    private Spinner spinnerCategoria;
    private Spinner spinnerGrupo;
    private EditText etBuscar;
    private ListView listView;
    private EjercicioAdapter adapter;
    private RealmResults<Ejercicios> plantillas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_bank);

        realm = Realm.getDefaultInstance();

        spinnerCategoria = findViewById(R.id.spinnerFiltroCategoria);
        spinnerGrupo = findViewById(R.id.spinnerFiltroGrupo);
        etBuscar = findViewById(R.id.etBuscarPlantilla);
        listView = findViewById(R.id.lvPlantillasEjercicios);
        Button btnNuevaPlantilla = findViewById(R.id.btnNuevaPlantilla);

        setupFilters();

        // Usa el nuevo parámetro (context, list, video callback)
        adapter = new EjercicioAdapter(this, null, this::abrirVideoTecnica);
        listView.setAdapter(adapter);

        plantillas = realm.where(Ejercicios.class)
                .equalTo("plantilla", true)
                .findAll();

        refreshList();

        btnNuevaPlantilla.setOnClickListener(v -> mostrarDialogoPlantilla(null));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Ejercicios ejercicio = (Ejercicios) adapter.getItem(position);
            if (ejercicio == null) {
                return;
            }
            mostrarAccionesPlantilla(ejercicio);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Ejercicios ejercicio = (Ejercicios) adapter.getItem(position);
            if (ejercicio != null) {
                confirmarEliminarPlantilla(ejercicio);
            }
            return true;
        });

        findViewById(R.id.btnCerrarBanco).setOnClickListener(v -> finish());
    }

    private void setupFilters() {
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, CATEGORIAS);
        spinnerCategoria.setAdapter(categoriaAdapter);

        ArrayAdapter<String> grupoAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, GRUPOS);
        spinnerGrupo.setAdapter(grupoAdapter);

        spinnerCategoria.setOnItemSelectedListener(new SimpleItemSelectedListener(this::refreshList));
        spinnerGrupo.setOnItemSelectedListener(new SimpleItemSelectedListener(this::refreshList));

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void refreshList() {
        if (plantillas == null) {
            return;
        }

        String categoriaFiltro = spinnerCategoria.getSelectedItem() != null
                ? spinnerCategoria.getSelectedItem().toString() : "Todas";
        String grupoFiltro = spinnerGrupo.getSelectedItem() != null
                ? spinnerGrupo.getSelectedItem().toString() : "Todos";
        String texto = etBuscar.getText() != null ? etBuscar.getText().toString().trim().toLowerCase() : "";

        List<Ejercicios> filtrados = new ArrayList<>();

        for (Ejercicios plantilla : plantillas) {
            if (plantilla == null) {
                continue;
            }

            boolean matchCategoria = "Todas".equals(categoriaFiltro)
                    || categoriaFiltro.equalsIgnoreCase(safeText(plantilla.getCategoria()));
            boolean matchGrupo = "Todos".equals(grupoFiltro)
                    || grupoFiltro.equalsIgnoreCase(safeText(plantilla.getGrupoMuscular()));

            String nombre = safeText(plantilla.getNombre()).toLowerCase();
            boolean matchTexto = texto.isEmpty() || nombre.contains(texto);

            if (matchCategoria && matchGrupo && matchTexto) {
                filtrados.add(plantilla);
            }
        }

        adapter.updateData(filtrados);
    }

    private void mostrarAccionesPlantilla(Ejercicios ejercicio) {
        String[] opciones = {"Usar en rutina", "Editar plantilla"};
        new AlertDialog.Builder(this)
                .setTitle(ejercicio.getNombre())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        seleccionarPlantilla(ejercicio);
                    } else {
                        mostrarDialogoPlantilla(ejercicio);
                    }
                })
                .show();
    }

    private void seleccionarPlantilla(Ejercicios ejercicio) {
        Intent data = new Intent();
        if (ejercicio.getId() != null) {
            data.putExtra(EXTRA_TEMPLATE_ID, ejercicio.getId().toHexString());
            setResult(RESULT_OK, data);
        }
        finish();
    }

    private void mostrarDialogoPlantilla(Ejercicios ejercicioEditable) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exercise_template, null, false);

        Spinner spCategoria = dialogView.findViewById(R.id.spinnerCategoriaPlantilla);
        Spinner spGrupo = dialogView.findViewById(R.id.spinnerGrupoPlantilla);
        Spinner spImagen = dialogView.findViewById(R.id.spinnerImagenPlantilla);
        EditText etNombre = dialogView.findViewById(R.id.etNombrePlantilla);
        EditText etSeries = dialogView.findViewById(R.id.etSeriesPlantilla);
        EditText etReps = dialogView.findViewById(R.id.etRepsPlantilla);
        EditText etPeso = dialogView.findViewById(R.id.etPesoPlantilla);
        EditText etVideo = dialogView.findViewById(R.id.etVideoPlantilla);

        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Fuerza", "Cardio", "Flexibilidad"});
        ArrayAdapter<String> grupoAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Pecho", "Espalda", "Hombro", "Biceps", "Triceps", "Pierna", "Core"});
        ArrayAdapter<String> imagenAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, IMAGE_LABELS);

        spCategoria.setAdapter(categoriaAdapter);
        spGrupo.setAdapter(grupoAdapter);
        spImagen.setAdapter(imagenAdapter);

        if (ejercicioEditable != null) {
            etNombre.setText(ejercicioEditable.getNombre());
            etSeries.setText(String.valueOf(ejercicioEditable.getSeries()));
            etReps.setText(String.valueOf(ejercicioEditable.getRepeticiones()));
            etPeso.setText(ejercicioEditable.getPeso());
            etVideo.setText(ejercicioEditable.getUrlVideo());
            seleccionarSpinnerPorTexto(spCategoria, ejercicioEditable.getCategoria());
            seleccionarSpinnerPorTexto(spGrupo, ejercicioEditable.getGrupoMuscular());
            seleccionarImagen(spImagen, ejercicioEditable.getImage());
        }

        new AlertDialog.Builder(this)
                .setTitle(ejercicioEditable == null ? "Nueva plantilla" : "Editar plantilla")
                .setView(dialogView)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = safeText(etNombre.getText() != null ? etNombre.getText().toString() : "");
                    String seriesText = safeText(etSeries.getText() != null ? etSeries.getText().toString() : "");
                    String repsText = safeText(etReps.getText() != null ? etReps.getText().toString() : "");
                    String peso = safeText(etPeso.getText() != null ? etPeso.getText().toString() : "");
                    String video = safeText(etVideo.getText() != null ? etVideo.getText().toString() : "");

                    if (nombre.isEmpty() || seriesText.isEmpty() || repsText.isEmpty() || peso.isEmpty()) {
                        Toast.makeText(this, "Completa nombre, series, reps y peso", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int series = Integer.parseInt(seriesText);
                        int reps = Integer.parseInt(repsText);
                        String categoria = spCategoria.getSelectedItem().toString();
                        String grupo = spGrupo.getSelectedItem().toString();
                        int imageRes = IMAGE_RESOURCES[spImagen.getSelectedItemPosition()];

                        realm.executeTransaction(r -> {
                            Ejercicios objetivo;
                            if (ejercicioEditable == null || ejercicioEditable.getId() == null) {
                                objetivo = r.createObject(Ejercicios.class, new ObjectId());
                            } else {
                                objetivo = r.where(Ejercicios.class)
                                        .equalTo("id", ejercicioEditable.getId())
                                        .findFirst();
                                if (objetivo == null) {
                                    objetivo = r.createObject(Ejercicios.class, new ObjectId());
                                }
                            }

                            objetivo.setNombre(nombre);
                            objetivo.setSeries(series);
                            objetivo.setRepeticiones(reps);
                            objetivo.setPeso(peso);
                            objetivo.setCategoria(categoria);
                            objetivo.setGrupoMuscular(grupo);
                            objetivo.setImage(imageRes);
                            objetivo.setUrlVideo(video);
                            objetivo.setDescripcion("Plantilla del entrenador");
                            objetivo.setNombrePupilo("");
                            objetivo.setCompletado(false);
                            objetivo.setPlantilla(true);
                            objetivo.setFechaAsignacion("");
                        });

                        refreshList();
                        Toast.makeText(this, "Plantilla guardada", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Series y repeticiones deben ser numeros", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void confirmarEliminarPlantilla(Ejercicios ejercicio) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar plantilla")
                .setMessage("Se eliminara \"" + safeText(ejercicio.getNombre()) + "\" del banco.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    realm.executeTransaction(r -> {
                        Ejercicios objetivo = r.where(Ejercicios.class)
                                .equalTo("id", ejercicio.getId())
                                .findFirst();
                        if (objetivo != null) {
                            objetivo.deleteFromRealm();
                        }
                    });
                    refreshList();
                })
                .show();
    }

    private void abrirVideoTecnica(Ejercicios ejercicio) {
        if (ejercicio == null || ejercicio.getUrlVideo() == null || ejercicio.getUrlVideo().trim().isEmpty()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ejercicio.getUrlVideo()));
        startActivity(intent);
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

    private void seleccionarImagen(Spinner spinner, int imageRes) {
        for (int i = 0; i < IMAGE_RESOURCES.length; i++) {
            if (IMAGE_RESOURCES[i] == imageRes) {
                spinner.setSelection(i);
                return;
            }
        }
        spinner.setSelection(0);
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
}

