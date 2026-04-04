package com.example.sportsgo;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatActivity extends AppCompatActivity {
    private TextView tvNombre, tvEstado;
    private View viewEstadoColor;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private ListView lvMensajes;

    private DatabaseReference chatRef, estadoRef, miEstadoRef;
    private String miNombre, nombreOtro, chatID;
    private Realm realm;
    private List<String> listaMensajesUI;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        realm = Realm.getDefaultInstance();


        //Obtener los datos de quienes hablan
        miNombre = getSharedPreferences("PrefeSportsGO", MODE_PRIVATE).getString("user_nombre", "Yo");
        nombreOtro = getIntent().getStringExtra("nombre_otro");

        //Crear un ID de chat unico(ordenando los nombres alfabeticamente)
        if(miNombre.compareTo(nombreOtro) < 0)chatID = miNombre + "_"+ nombreOtro;
        else chatID = nombreOtro + "_" +miNombre;

        initUI();
        configurarPresencia();
        escucharMensajes();

        btnEnviar.setOnClickListener(v -> enviarMensaje());

    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if(texto.isEmpty()) return;

        //Creamos el objeto del mensaje
        Message mensaje = new Message();
        mensaje.setEmisor(miNombre);
        mensaje.setReceptor(nombreOtro);
        mensaje.setTexto(texto);
        mensaje.setTimestamp(System.currentTimeMillis());

        //Enviar a firebase para que la otra persona lo reciba
        FirebaseDatabase.getInstance().getReference("chats").child(chatID).push().setValue(mensaje);

        //Guardar en Realm, es decir en local
        realm.executeTransaction(r -> r.copyToRealm(mensaje));

        etMensaje.setText("");
        actualizarListaDesdeRealm();
    }

    private void escucharMensajes(){
        //Escuchamos los nuevos mensajes en la sala compartida de Firebase
        FirebaseDatabase.getInstance().getReference("chats").child(chatID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message m = snapshot.getValue(Message.class);

                //Si el mensaje es de la otra persona, lo guardamos localmente con Realm
                if(m != null && !m.getEmisor().equals(miNombre)){
                    realm.executeTransaction(r -> r.copyToRealmOrUpdate(m));
                    actualizarListaDesdeRealm();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Inicializamos las referencias con el XML para usar los widgets
    private void initUI(){
        tvNombre = findViewById(R.id.tvNombreChat);
        tvEstado = findViewById(R.id.tvEstadoTexto);
        viewEstadoColor = findViewById(R.id.viewEstadoColor);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviarMsg);
        lvMensajes = findViewById(R.id.lvMensajes);

        tvNombre.setText(nombreOtro);

        //Inicializamos la lista de la pantalla
        listaMensajesUI = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMensajesUI);
        lvMensajes.setAdapter(adapter);

        actualizarListaDesdeRealm();
    }


    private void actualizarListaDesdeRealm() {
        //Consultamos los mensajes de este chat ordenados por tiempo
        RealmResults<Message> historial = realm.where(Message.class).sort("timestamp", Sort.ASCENDING).findAll();

        listaMensajesUI.clear();
        for(Message m : historial){
            String prefijo = m.getEmisor().equals(miNombre) ? "Yo: " : m.getEmisor() + ": ";
            listaMensajesUI.add(prefijo + m.getTexto());
        }
        adapter.notifyDataSetChanged();
        //Hacemos scroll hasta el ultimo mensaje
        lvMensajes.setSelection(adapter.getCount() - 1);
    }

    private void configurarPresencia(){
        //Referencia de mi estado en la nube
        miEstadoRef = FirebaseDatabase.getInstance().getReference("presencia/"+miNombre);
        //Referencia del estado de la otra persona en la nube
        estadoRef = FirebaseDatabase.getInstance().getReference("presencia/"+nombreOtro);

        //Sistema para detectar si yo estoy conectado
        DatabaseReference conectadoRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        conectadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean conectado = snapshot.getValue(Boolean.class);

                if(conectado){
                    miEstadoRef.setValue("online");
                    //Si cierro el programa o pierdo conexion, firebase me pondra offline
                    miEstadoRef.onDisconnect().setValue("offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        estadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String estado = snapshot.getValue(String.class);
                if("online".equals(estado)){
                    tvEstado.setText("En linea");
                    viewEstadoColor.setBackgroundColor(Color.GREEN);
                }else{
                    tvEstado.setText("Desconectado");
                    viewEstadoColor.setBackgroundColor(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Al salir del chat, marcamos como offline
        if (miEstadoRef != null) miEstadoRef.setValue("offline");
        if (realm != null) realm.close();
    }
}


