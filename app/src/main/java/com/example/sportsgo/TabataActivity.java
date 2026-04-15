package com.example.sportsgo;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class TabataActivity extends AppCompatActivity {
    private TextView tvStatus, tvCountdown, tvRoundsLeft;
    private TextInputEditText etRounds, etWork, etRest;
    private Button btnStart;

    private CountDownTimer timer;
    private int rondasTotales, rondasRestantes;
    private long tiempoTrabajo, tiempoDescanso;
    private boolean esTiempoDeTrabajo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabata);

        tvStatus = findViewById(R.id.tvStatus);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvRoundsLeft = findViewById(R.id.tvRoundsLeft);
        etRounds = findViewById(R.id.etRounds);
        etWork = findViewById(R.id.etWork);
        etRest = findViewById(R.id.etRest);
        btnStart = findViewById(R.id.btnStartTabata);

        btnStart.setOnClickListener(v -> inciarEntrenamiento());

    }

    private void inciarEntrenamiento() {
        try{
            rondasTotales = Integer.parseInt(etRounds.getText().toString());
            rondasRestantes = rondasTotales;
            tiempoTrabajo = Long.parseLong(etWork.getText().toString()) *1000;
            tiempoDescanso = Long.parseLong(etRest.getText().toString()) *1000;

            btnStart.setEnabled(false);
            etRounds.setEnabled(false);
            etWork.setEnabled(false);
            etRest.setEnabled(false);

            proximaFase();
        }catch(Exception e){
            Toast.makeText(this, "Introduce los valores que sean válidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void proximaFase() {
        if(rondasRestantes <= 0){
            finalizarTabata();
            return;
        }
        if(esTiempoDeTrabajo){
            tvStatus.setText("¡Dale caña!");
            tvStatus.setTextColor(android.graphics.Color.GREEN);
            tvRoundsLeft.setText("Ronda: "+ (rondasTotales - rondasRestantes +1 ) +" / "+ rondasTotales);
            iniciarContador(tiempoTrabajo);
        }else{
            tvStatus.setText("DESCANSA");
            tvStatus.setTextColor(android.graphics.Color.YELLOW);
            iniciarContador(tiempoDescanso);
        }
    }

    private void iniciarContador(long milisegundos) {
        timer = new CountDownTimer(milisegundos, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if(esTiempoDeTrabajo){
                    esTiempoDeTrabajo = false;
                }else{
                    esTiempoDeTrabajo = true;
                    rondasRestantes--;
                }
                proximaFase();
            }
        }.start();
    }

    private void finalizarTabata() {
        tvStatus.setText("¡TERMINADO!");
        tvStatus.setTextColor(android.graphics.Color.WHITE);
        tvCountdown.setText("00");
        btnStart.setEnabled(true);
        etRounds.setEnabled(true);
        etWork.setEnabled(true);
        etRest.setEnabled(true);
        Toast.makeText(this, "¡Excelente sesón!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(timer != null){
            timer.cancel();
        }
    }
}