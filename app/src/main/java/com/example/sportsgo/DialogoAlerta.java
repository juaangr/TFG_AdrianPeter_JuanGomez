package com.example.sportsgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.SportsGO.R;

public class DialogoAlerta {
    public interface OnResultadoDialogo{
        void alAceptar();
    }
    public static void mostrar(Context context, OnResultadoDialogo listener){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cookies, null);
        CheckBox checkBox = view.findViewById(R.id.cbAceptar);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cookies y preferencias");
        builder.setView(view);
        builder.setCancelable(false);

        builder.setPositiveButton("Continuar", ((dialog, which) -> {
            if (checkBox.isChecked()) {
                listener.alAceptar();
            }else{
                Toast.makeText(context, "Debes aceptar las cookies para continuar",Toast.LENGTH_SHORT).show();
            }
        }));
        builder.setNegativeButton("Cerrar", ((dialog, which) -> dialog.dismiss()));
        builder.show();
    }
}
