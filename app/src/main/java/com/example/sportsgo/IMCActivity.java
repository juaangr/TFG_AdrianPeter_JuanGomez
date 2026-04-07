package com.example.sportsgo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class IMCActivity extends AppCompatActivity {
    private TextInputEditText etPeso, etAltura;
    private Button btnCalcular;
    private TextView tvResultadoNum, tvResultadoTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        btnCalcular = findViewById(R.id.btnCalcularIMC);
        tvResultadoNum = findViewById(R.id.tvResultadoNum);
        tvResultadoTexto = findViewById(R.id.tvResultadoTexto);

        btnCalcular.setOnClickListener(v -> calcular());
    }

    private void calcular() {
        String sPeso = etPeso.getText().toString();
        String sAltura = etAltura.getText().toString();

        if(sPeso.isEmpty() || sAltura.isEmpty()){
            Toast.makeText(this, "Por favor, rellena todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        float peso = Float.parseFloat(sPeso);
        float alturaCm = Float.parseFloat(sAltura);
        float alturaM = alturaCm / 100;

        //Formula de Peso partido de Altura al cuadrado
        float imc = peso / (alturaM * alturaM);

        //Mostrar numero con un decimal
        tvResultadoNum.setText(String.format("%.1f", imc));

        //Determinar el rango
        if(imc <18.5){
            tvResultadoTexto.setText("BAJO PESO");
            tvResultadoTexto.setTextColor(android.graphics.Color.YELLOW);
        }else if(imc <25){
            tvResultadoTexto.setText("PESO NORMAL (SALUDABLE)");
            tvResultadoTexto.setTextColor(android.graphics.Color.GREEN);
        }else if (imc < 30){
            tvResultadoTexto.setText("SOBREPESO");
            tvResultadoTexto.setTextColor(android.graphics.Color.parseColor("#FF8C00"));
        }else{
            tvResultadoTexto.setText("OBESIDAD");
            tvResultadoTexto.setTextColor(android.graphics.Color.RED);
        }
    }

}
