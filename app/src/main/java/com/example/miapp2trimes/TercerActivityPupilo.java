package com.example.miapp2trimes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.SportsGO.R;

import java.util.ArrayList;

public class TercerActivityPupilo extends AppCompatActivity {
    private Button button;
    private ListView listView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tercer_activity);
        button = findViewById(R.id.btnFinalizar);
        listView = findViewById(R.id.lvEjercicios);

        ArrayList<Ejercicios> ejercicios = new ArrayList<>();
        ejercicios.add(new Ejercicios("Press Banca", R.drawable.press,4,12,"40Kg"));
        ejercicios.add(new Ejercicios("Curl de Biceps", R.drawable.biceps,3,8,"30Kg"));
        ejercicios.add(new Ejercicios("Sentadillas", R.drawable.pierna,5,12,"60Kg"));
        ejercicios.add(new Ejercicios("Triceps", R.drawable.triceps,6,10,"10Kg"));
        ejercicios.add(new Ejercicios("Hombro", R.drawable.hombro,4,14,"5Kg"));

        EjercicioAdapter adapter = new EjercicioAdapter(TercerActivityPupilo.this, ejercicios);
        listView.setAdapter(adapter);

        button.setOnClickListener(v ->
                Toast.makeText(this, "Sesion finalizada con exito ¡Vuelve pronto!", Toast.LENGTH_SHORT).show());
                //Intent intent = new Intent(this, MainActivity.class);
                //startActivity(intent);
    }
}
