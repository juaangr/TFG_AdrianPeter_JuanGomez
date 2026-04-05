package com.example.sportsgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

public class PupiloProgresoAdapter extends BaseAdapter{
    private Context context;
    private RealmResults<Usuario> pupilos;
    private Realm realm;

    public PupiloProgresoAdapter(Context context, RealmResults<Usuario> pupilos, Realm realm) {
        this.context = context;
        this.pupilos = pupilos;
        this.realm = realm;
    }

    @Override
    public int getCount() {
        return pupilos.size();
    }

    @Override
    public Object getItem(int i) {
        return pupilos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pupilo_trainer,parent,false);
        }
        Usuario pupilo = pupilos.get(position);
        TextView nombre = convertView.findViewById(R.id.txtNombrePupilo);
        TextView progreso = convertView.findViewById(R.id.txtProgreso);

        if(pupilo != null) {
            nombre.setText(pupilo.getNombre());

            //Contamos los ejercicios totales del alumno
            long totales = realm.where(Ejercicios.class).equalTo("nombrePupilo", pupilo.getNombre()).count();

            //Contamos los ejercicios completados
            long completados = realm.where(Ejercicios.class).equalTo("nombrePupilo", pupilo.getNombre()).equalTo("completado", true).count();

            progreso.setText(completados+" / "+totales);

            //Cambiamos color si ha terminado todo
            if(totales > 0 && completados ==totales){
                progreso.setTextColor(android.graphics.Color.parseColor("#38A169")); //Verde
            }
        }
        return convertView;
    }
}
