package com.example.sportsgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTemplateAdapter extends BaseAdapter {

    public interface OnVideoClickListener {
        void onVideoClick(Ejercicios ejercicio);
    }

    private final Context context;
    private final List<Ejercicios> data = new ArrayList<>();
    private final OnVideoClickListener videoClickListener;

    public ExerciseTemplateAdapter(Context context, OnVideoClickListener videoClickListener) {
        this.context = context;
        this.videoClickListener = videoClickListener;
    }

    public void updateData(List<Ejercicios> ejercicios) {
        data.clear();
        if (ejercicios != null) {
            data.addAll(ejercicios);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exercise_template, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.imgTemplateEjercicio);
            holder.nombre = convertView.findViewById(R.id.txtTemplateNombreEj);
            holder.meta = convertView.findViewById(R.id.txtTemplateMeta);
            holder.detalles = convertView.findViewById(R.id.txtTemplateDetalles);
            holder.video = convertView.findViewById(R.id.btnVerTecnica);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Ejercicios ejercicio = data.get(position);
        holder.nombre.setText(ejercicio.getNombre());

        String categoria = ejercicio.getCategoria() == null || ejercicio.getCategoria().trim().isEmpty()
                ? "Sin categoria"
                : ejercicio.getCategoria();
        String grupo = ejercicio.getGrupoMuscular() == null || ejercicio.getGrupoMuscular().trim().isEmpty()
                ? "Sin grupo"
                : ejercicio.getGrupoMuscular();
        holder.meta.setText(categoria + " | " + grupo);
        holder.detalles.setText(ejercicio.getSeries() + " series x " + ejercicio.getRepeticiones() + " reps | " + ejercicio.getPeso());

        int imageRes = ejercicio.getImage() != 0 ? ejercicio.getImage() : R.mipmap.ic_launcher;
        holder.image.setImageResource(imageRes);

        String urlVideo = ejercicio.getUrlVideo();
        boolean hasVideo = urlVideo != null && !urlVideo.trim().isEmpty();
        holder.video.setVisibility(hasVideo ? View.VISIBLE : View.GONE);
        holder.video.setOnClickListener(null);
        if (hasVideo) {
            holder.video.setOnClickListener(v -> videoClickListener.onVideoClick(ejercicio));
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView nombre;
        TextView meta;
        TextView detalles;
        Button video;
    }
}

