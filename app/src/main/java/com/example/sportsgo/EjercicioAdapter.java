package com.example.sportsgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import io.realm.RealmResults;

//ESTE ADAPTADOR TIENE LA FUNCION DE QUE EL TRAINER LE AÑADA UN EJERCICIO EN LA NUBE Y APAREZCA AQUI AUTOMATICAMENTE
public class EjercicioAdapter extends RealmBaseAdapter<Ejercicios> implements ListAdapter {
    private Context context;

    public EjercicioAdapter(Context context, RealmResults<Ejercicios> data ) {
        //Implemenatamos la coleccion de datos de la superclase
        super(data);
        this.context = context;
    }
    @Override
    public View getView(int position,View convertView, ViewGroup parent){
        ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(context)
        }

    }
}

