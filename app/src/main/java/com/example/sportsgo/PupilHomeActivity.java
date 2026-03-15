package com.example.sportsgo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import io.realm.Realm;

public class PupilHomeActivity extends AppCompatActivity {

    private TextView tvCalorias;
    private TextInputEditText etPeso, etAltura, etEdad;
    private AutoCompleteTextView spinnerObjetivo;
    private RadioGroup rgSexo, rgActividad;
    private RadioButton rbH;
    private Button btnCalcular, btnEntrenamientos;
    private int caloriasFinales = 0;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.segundo_activity);

        // TextView en el cual se veran reflejadas las calorias
        tvCalorias = findViewById(R.id.txtView2);

        //EditText de peso, altura y edad
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        etEdad = findViewById(R.id.etEdad);

        //RadioGroup del sexo
        rgSexo = findViewById(R.id.rgSexo);

        //Spinner de objetivos
        spinnerObjetivo = findViewById(R.id.spinnerObjetivo);

        //RadioGroup de la actividad diaria del pupilo
        rgActividad = findViewById(R.id.rgActividad);

        //RadioButton del sexo del pupilo que en este caso solo declaramos la de hombre
        rbH = findViewById(R.id.rbHombre);

        //Botones de calcular y continuar
        btnCalcular = findViewById(R.id.btnCalcular);
        btnEntrenamientos = findViewById(R.id.btnContinuar);

        // Spinner objetivos el cual le tenemos ligado a el AutoCompleteTextView
        String[] objetivos = {"Bajar de peso", "Mantener peso", "Subir de peso"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                objetivos
        );
        //Aqui declaramos el spinner de objetivos
        spinnerObjetivo.setAdapter(adapter);

        //Accion del boton de calcular
        btnCalcular.setOnClickListener(v -> calcularCalorias());

        // Boton de continuar en el que cuando la condicion tiene mas de 0 calorias
        // se pasa a la siguiente actividad al dar al boton de ver entrenamientos
        btnEntrenamientos.setOnClickListener(v -> {
            if (caloriasFinales > 0) {
                //Llamamos al dialogo de privacidad antes de continuar
                mostrarDialogoPrivacidad();
            } else {
                Toast.makeText(this, "Debes calcular las calorías primero", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Metodo parra mostrar la ventana de privacidad del pupilo
    private void mostrarDialogoPrivacidad(){
        com.google.android.material.dialog.MaterialAlertDialogBuilder builder =
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(this);

        builder.setTitle("Privacidad de datos");
        builder.setMessage("Para un seguimiento óptimo, ¿deseas compartir todos tus datos con el/los trainer/s o solo las calorias?");

        //Icono
        builder.setIcon(R.drawable.ic_launcher_foreground);

        //Opcion 1: Compartir todos los datos
        builder.setPositiveButton("Compartir todo", (dialog, which) ->{
            irATercerActivity(true); //El true nos da permiso total para tranferir todos los datos al trainer/s
        });

        //Opcion 2 : Solo las calorias
        builder.setNegativeButton("Solo calorias", (dialog, which) -> {
            irATercerActivity(false); // El false nos permite solo transferir sus calorias
        });

        //Evitar que cierren el dialogo pulsando fuera de el, es decir que esta obligado a elegir una opcion
        builder.setCancelable(false);
        builder.show();
    }

    //Metodo para que el usuario pase a la siguiente pagina enviando su decision
    private void irATercerActivity(boolean permisoCompleto){
        Realm realm = Realm.getDefaultInstance();

        //Obtenemos el email que se introdujo
        SharedPreferences sharedPreferences = getSharedPreferences("PrefeSportsGO", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email","");

        realm.executeTransaction(r -> {
            Usuario user = r.where(Usuario.class).equalTo("email", userEmail).findFirst();
            if(user != null){
                user.setCalorias(caloriasFinales);
                user.setPermisoCompleto(permisoCompleto);
                //Guardamos peso y altura solo si el calculo fue existoso
                user.setPeso(Double.parseDouble(etPeso.getText().toString()));
                user.setAltura(Double.parseDouble(etAltura.getText().toString()));
            }
        });
        Intent intent = new Intent(this, TrainerDashboardActivity.class);
        startActivity(intent);
    }

    //El metodo del calculo total de calorias del pupilo
    private void calcularCalorias() {
        String strPeso = etPeso.getText().toString();
        String strAltura = etAltura.getText().toString();
        String strEdad = etEdad.getText().toString();

        //Condicion para que todos los campos esten obligatoriamente rellenos para continuar
        if (etPeso.getText().toString().isEmpty() || etAltura.getText().toString().isEmpty() || etEdad.getText().toString().isEmpty() || rgSexo.getCheckedRadioButtonId() == -1 || rgActividad.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Faltan algunos campos por complentar", Toast.LENGTH_SHORT).show();
            return;
        }
            double peso = Double.parseDouble(strPeso);
            double altura = Double.parseDouble(strAltura);
            int edad = Integer.parseInt(strEdad);

            // Formula TMB Harris-Benedict
            double tmb;
            if(rbH.isChecked()){
                tmb = (10*peso) + (6.25*altura) -(5*edad) +5;
            }else{
                tmb = (10*peso) + (6.25*altura) -(5*edad) -161;
            }

            //Factor de actividad diaria del usuario
            double factor;
            int idAct = rgActividad.getCheckedRadioButtonId();
            if(idAct == R.id.rbSedentario) factor = 1.2;
            else if(idAct == R.id.rbLigero) factor =1.375;
            else if(idAct == R.id.rbModerado) factor = 1.55;
            else factor = 1.725;

            //Objetivo (Subir, mantener o bajar de peso)
            int ajuste = 0;
            String objetivo = spinnerObjetivo.getText().toString();
            if(objetivo.equals("Bajar de peso")) ajuste = -500;
            else if(objetivo.equals("Subir de peso")) ajuste = 500;

            //Calorias finales
            caloriasFinales = (int) ((tmb * factor) + ajuste);
            tvCalorias.setText("Calorias diarias: " +caloriasFinales + " kcal");
        }
}
