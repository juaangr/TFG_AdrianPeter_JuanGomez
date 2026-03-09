package com.example.miapp2trimes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miapp2trimes.Ejercicios;

import java.util.List;

public class EjercicioAdapter extends ArrayAdapter<Ejercicios> {

    public EjercicioAdapter(Context context, List<Ejercicios> ejercicios) {
        super(context, 0, ejercicios);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Ejercicios ejercicio = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_ejercicios, parent, false);
        }

        ImageView img = convertView.findViewById(R.id.imgEjercicio);
        TextView nombre = convertView.findViewById(R.id.txtNombreEj);
        TextView detalles = convertView.findViewById(R.id.txtDetallesEj);
        TextView peso = convertView.findViewById(R.id.txtPesoEj);

        nombre.setText(ejercicio.getNombre());
        detalles.setText(
                ejercicio.getSeries() + " series x " +
                ejercicio.getRepeticiones() + " reps"
        );
        peso.setText("Peso inicial: " + ejercicio.getPeso());
        img.setImageResource(ejercicio.getImagen());

        return convertView;
    }
}
