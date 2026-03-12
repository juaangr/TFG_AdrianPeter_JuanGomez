package com.example.sportsgo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class SegundoActivityPupilo extends AppCompatActivity {

    private TextView tvInfoEdad, tvCalorias;
    private EditText etPeso, etAltura;
    private AutoCompleteTextView spinner;
    private RadioGroup rgSexo;
    private RadioButton rgH, rgM;
    private RadioButton rbSedentario, rbLigero, rbModerado, rbFuerte;
    private Button btnCalcular, btnEntrenamientos;
    private int edad;
    private int caloriasFinales = 0;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.segundo_activity);

        // Referencias UI
        tvInfoEdad = findViewById(R.id.tvInfoEdad);
        tvCalorias = findViewById(R.id.txtView2);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        rgSexo = findViewById(R.id.rgSexo);
        spinner = findViewById(R.id.spinnerObjetivo);

        rbSedentario = findViewById(R.id.rbSedentario);
        rbLigero = findViewById(R.id.rbLigero);
        rbModerado = findViewById(R.id.rbModerado);
        rbFuerte = findViewById(R.id.rbFuerte);

        rgH = findViewById(R.id.rbHombre);
        rgM = findViewById(R.id.rbMujer);

        btnCalcular = findViewById(R.id.btnCalcular);
        btnEntrenamientos = findViewById(R.id.btnContinuar);

        // Spinner objetivos el cual le tenemos ligado a el AutoCompleteTextView
        String[] objetivos = {"Bajar de peso", "Mantener peso", "Subir de peso"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                objetivos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Recibir edad
        edad = getIntent().getIntExtra("key_edad", -1);
        tvInfoEdad.setText("Tienes: " + edad + " años");

        btnCalcular.setOnClickListener(v -> calcularCalorias());

        btnEntrenamientos.setOnClickListener(v -> {
            if (caloriasFinales <= 0) {
                Toast.makeText(this, "Debes calcular las calorías primero", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(SegundoActivityPupilo.this, TercerActivityPupilo.class);
                intent.putExtra("key_calorias", caloriasFinales);
                startActivity(intent);
            }
        });
    }

    private void calcularCalorias() {
        String strPeso = etPeso.getText().toString();
        String strAltura = etAltura.getText().toString();

        if (etPeso.getText().toString().isEmpty() || etAltura.getText().toString().isEmpty()) {
            Toast.makeText(this, "Rellena peso y altura", Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            double peso = Double.parseDouble(etPeso.getText().toString());
            double altura = Double.parseDouble(etAltura.getText().toString());

            if (rgSexo.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Selecciona el sexo", Toast.LENGTH_SHORT).show();
                return;
            }

            // TMB (Harris-Benedict)
            double tmb;
            if (rgH.isChecked()) {
                tmb = (10 * peso) + (6.25 * altura) - (5 * edad) + 5;
            } else {
                tmb = (10 * peso) + (6.25 * altura) - (5 * edad) - 161;
            }

            // Nos traemos el objetivo seleccionado por el pupilo
            String objetivo = spinner.getText().toString();
            int ajusteObjetivo = 0;
            if (objetivo.equals("Bajar de peso")) ajusteObjetivo = -500;
            else if (objetivo.equals("Subir de peso")) ajusteObjetivo = 500;

            // Actividad física
            double factorActividad;
            if (rbSedentario.isChecked()) {
                factorActividad = 1.2;
            } else if (rbLigero.isChecked()) {
                factorActividad = 1.375;
            } else if (rbModerado.isChecked()) {
                factorActividad = 1.55;
            } else if (rbFuerte.isChecked()) {
                factorActividad = 1.725;
            } else {
                Toast.makeText(this, "Selecciona nivel de actividad", Toast.LENGTH_SHORT).show();
                return;
            }

            caloriasFinales = (int) ((tmb * factorActividad) + ajusteObjetivo);
            tvCalorias.setText("Calorías diarias recomendadas: " + caloriasFinales);
        }catch (Exception e){
            Toast.makeText(this, "Error al introducir los datos", Toast.LENGTH_SHORT).show();
        }
        }

}
