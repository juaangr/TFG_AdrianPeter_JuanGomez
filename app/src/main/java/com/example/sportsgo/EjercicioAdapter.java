package com.example.sportsgo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CheckBox;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.List;
import java.util.ArrayList;

public class EjercicioAdapter extends BaseAdapter {

    public interface OnVideoClickListener {
        void onVideoClick(Ejercicios ejercicio);
    }

    private Context context;
    private List<Ejercicios> adapterList;
    private RealmResults<Ejercicios> adapterDataRealm;
    
    private boolean isPupilMode;
    private OnVideoClickListener videoClickListener;

    // Constructor para el modo "Pupilo" (Con Checkbox y Realm)
    public EjercicioAdapter(Context context, RealmResults<Ejercicios> data) {
        this.context = context;
        this.adapterDataRealm = data;
        this.isPupilMode = true;
    }

    // Constructor para el modo "Plantilla / Banco de ejercicios" (Con Video y sin Realm)
    public EjercicioAdapter(Context context, List<Ejercicios> data, OnVideoClickListener videoClickListener) {
        this.context = context;
        this.adapterList = new ArrayList<>();
        if (data != null) {
            this.adapterList.addAll(data);
        }
        this.isPupilMode = false;
        this.videoClickListener = videoClickListener;
    }

    public void updateData(List<Ejercicios> ejercicios) {
        if (!isPupilMode && adapterList != null) {
            adapterList.clear();
            if (ejercicios != null) {
                adapterList.addAll(ejercicios);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (isPupilMode) {
            return adapterDataRealm != null ? adapterDataRealm.size() : 0;
        } else {
            return adapterList != null ? adapterList.size() : 0;
        }
    }

    @Override
    public Object getItem(int i) {
        if (isPupilMode) {
            return adapterDataRealm != null ? adapterDataRealm.get(i) : null;
        } else {
            return adapterList != null ? adapterList.get(i) : null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ejercicios, parent, false);
            holder = new ViewHolder();

            holder.imagen = convertView.findViewById(R.id.imgEjercicio);
            holder.nombre = convertView.findViewById(R.id.txtNombreEj);
            holder.meta = convertView.findViewById(R.id.txtMetaEj);
            holder.detalles = convertView.findViewById(R.id.txtDetallesEj);
            holder.peso = convertView.findViewById(R.id.txtPesoEj);
            holder.check = convertView.findViewById(R.id.cbCompletado);
            holder.video = convertView.findViewById(R.id.btnVerTecnica);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Ejercicios ejercicio = (Ejercicios) getItem(position);

        if (ejercicio != null) {
            holder.nombre.setText(ejercicio.getNombre());
            holder.detalles.setText(ejercicio.getSeries() + " series x " + ejercicio.getRepeticiones() + " reps");

            // Configurar Imagen
            if (ejercicio.getImage() != 0) {
                holder.imagen.setImageResource(ejercicio.getImage());
            } else {
                holder.imagen.setImageResource(R.mipmap.ic_launcher);
            }

            // Modo Pupilo activo
            if (isPupilMode) {
                holder.peso.setVisibility(View.VISIBLE);
                holder.peso.setText("Peso asignado: " + ejercicio.getPeso());
                
                holder.meta.setVisibility(View.GONE); // No es tan necesario para el pupilo
                bindVideoAction(holder.video, ejercicio);

                holder.check.setVisibility(View.VISIBLE);
                holder.check.setOnCheckedChangeListener(null);
                holder.check.setChecked(ejercicio.isCompletado());
                holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(r -> ejercicio.setCompletado(isChecked));
                    realm.close();

                    //Actualizamos ahora Firebase (nube)
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
            } else {
                // Modo Plantilla O Entrenador activo
                holder.peso.setVisibility(View.GONE);
                holder.check.setVisibility(View.GONE);
                
                holder.meta.setVisibility(View.VISIBLE);
                String categoria = ejercicio.getCategoria() == null || ejercicio.getCategoria().trim().isEmpty() ? "Sin categoria" : ejercicio.getCategoria();
                String grupo = ejercicio.getGrupoMuscular() == null || ejercicio.getGrupoMuscular().trim().isEmpty() ? "Sin grupo" : ejercicio.getGrupoMuscular();
                holder.meta.setText(categoria + " | " + grupo);
                
                // Mostrar datos extra de la plantilla
                holder.detalles.setText(ejercicio.getSeries() + " series x " + ejercicio.getRepeticiones() + " reps | " + ejercicio.getPeso());

                bindVideoAction(holder.video, ejercicio);
            }
        }
        return convertView;
    }

    private void bindVideoAction(Button videoButton, Ejercicios ejercicio) {
        if (videoButton == null || ejercicio == null) {
            return;
        }

        String urlVideo = ejercicio.getUrlVideo();
        boolean hasVideo = urlVideo != null && !urlVideo.trim().isEmpty();
        videoButton.setVisibility(hasVideo ? View.VISIBLE : View.GONE);
        videoButton.setOnClickListener(null);

        if (!hasVideo) {
            return;
        }

        videoButton.setOnClickListener(v -> {
            if (videoClickListener != null) {
                videoClickListener.onVideoClick(ejercicio);
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlVideo));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    private static class ViewHolder {
        ImageView imagen;
        TextView nombre;
        TextView meta;
        TextView detalles;
        TextView peso;
        CheckBox check;
        Button video;
    }
}
