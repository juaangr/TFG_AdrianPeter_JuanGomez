package com.example.sportsgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CheckBox;
import io.realm.Realm;
import io.realm.RealmResults;

public class EjercicioAdapter extends BaseAdapter {

    private Context context;
    private RealmResults<Ejercicios> adapterData;


    public EjercicioAdapter(Context context, RealmResults<Ejercicios> data) {
        this.adapterData = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return adapterData != null ? adapterData.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return adapterData != null ? adapterData.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Usamos tu archivo: item_ejercicios.xml
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ejercicios, parent, false);
            holder = new ViewHolder();

            // Vinculamos con los IDs de TU XML antiguo
            holder.imagen = convertView.findViewById(R.id.imgEjercicio);
            holder.nombre = convertView.findViewById(R.id.txtNombreEj);
            holder.detalles = convertView.findViewById(R.id.txtDetallesEj);
            holder.peso = convertView.findViewById(R.id.txtPesoEj);

            // Si vas a añadir un Checkbox al XML, asegúrate de que el ID sea cbCompletado
            holder.check = convertView.findViewById(R.id.cbCompletado);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Ejercicios ejercicio = adapterData.get(position);

        if (ejercicio != null) {
            holder.nombre.setText(ejercicio.getNombre());
            holder.detalles.setText(ejercicio.getSeries() + " series x " + ejercicio.getRepeticiones() + " reps");
            holder.peso.setText("Peso asignado: " + ejercicio.getPeso());

            // Mostramos la imagen (si tienes el ID del recurso guardado)
            if (ejercicio.getImage() != 0) {
                holder.imagen.setImageResource(ejercicio.getImage());
            }

            // Lógica del Checkbox para que el Trainer lo vea en tiempo real
            if (holder.check != null) {
                holder.check.setOnCheckedChangeListener(null);
                holder.check.setChecked(ejercicio.isCompletado());
                holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(r -> ejercicio.setCompletado(isChecked));
                    realm.close();

                    //Actualizamos ahora Firebase (nube) para que el trainer lo vea
                    com.google.firebase.database.FirebaseDatabase.getInstance()
                            .getReference("entrenamientos")
                            .child(ejercicio.getNombrePupilo())
                            .orderByChild("nombre").equalTo(ejercicio.getNombre())
                            .get().addOnSuccessListener(snapshot -> {
                                for (com.google.firebase.database.DataSnapshot ds : snapshot.getChildren()){
                                    ds.getRef().child("completado").setValue(isChecked);
                                }
                            });
                });
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView imagen;
        TextView nombre;
        TextView detalles;
        TextView peso;
        CheckBox check; // Asegúrate de añadirlo al XML para que el Trainer vea el progreso
    }
}
