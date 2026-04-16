package com.example.sportsgo;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiChatActivity extends AppCompatActivity {

    private ListView lvChat;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private List<String> chatMessages;
    private ArrayAdapter<String> adapter;

    private GenerativeModelFutures model;
    private String miNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemini_chat);

        // Recuperamos el nombre del usuario para el chat
        SharedPreferences prefs = getSharedPreferences("PrefeSportsGO", MODE_PRIVATE);
        miNombre = prefs.getString("user_nombre", "Usuario");

        lvChat = findViewById(R.id.lvChatGemini);
        etMensaje = findViewById(R.id.etMensajeGemini);
        btnEnviar = findViewById(R.id.btnEnviarGemini);

        chatMessages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatMessages);
        lvChat.setAdapter(adapter);

        // CONFIGURACIÓN DE GEMINI
        // Usamos el nombre completo del paquete para evitar problemas de importación con BuildConfig
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                com.example.sportsgo.BuildConfig.GEMINI_API_KEY
        );
        model = GenerativeModelFutures.from(gm);

        // Mensaje de bienvenida de la IA
        chatMessages.add("IA: ¡Hola! Soy tu asistente de seguridad. Pregúntame dudas sobre técnica o avísame si sientes molestias. No puedo cambiar tu rutina, pero te ayudaré a entrenar seguro.");
        adapter.notifyDataSetChanged();

        btnEnviar.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String query = etMensaje.getText().toString().trim();
        if (query.isEmpty()) return;

        chatMessages.add("Tú: " + query);
        adapter.notifyDataSetChanged();
        etMensaje.setText("");
        lvChat.setSelection(adapter.getCount() - 1);

        // System Prompt para que la IA sepa su rol de seguridad
        String systemPrompt = "Eres el Asistente de Seguridad de SportsGO. Tu rol es:\n" +
                "1. Resolver dudas sobre TÉCNICA de ejercicios (ej. '¿cómo pongo la espalda en sentadilla?').\n" +
                "2. Si el usuario menciona DOLOR ('me duele', 'pinchazo', 'molestia'), debes aconsejarle PARAR inmediatamente y recomendarle que informe a su entrenador.\n" +
                "3. NO puedes crear rutinas, solo explicar ejercicios existentes.\n" +
                "4. Si detectas que el usuario tiene un problema que requiere atención humana o informa de dolor, termina tu respuesta con la frase exacta: '[ALERTA_ENTRENADOR]'.\n" +
                "Sé profesional, breve y prioriza la salud del usuario.";

        Content content = new Content.Builder()
                .addText(systemPrompt + "\n\nUsuario: " + query)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                runOnUiThread(() -> {
                    if (resultText != null && resultText.contains("[ALERTA_ENTRENADOR]")) {
                        // Limpiamos el texto de la marca interna antes de mostrarlo
                        String finalMsg = resultText.replace("[ALERTA_ENTRENADOR]", "").trim();
                        chatMessages.add("IA: " + finalMsg);
                        mostrarDialogoAlertaEntrenador();
                    } else {
                        chatMessages.add("IA: " + resultText);
                    }
                    adapter.notifyDataSetChanged();
                    lvChat.setSelection(adapter.getCount() - 1);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(GeminiChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }, executor);
    }

    private void mostrarDialogoAlertaEntrenador() {
        new AlertDialog.Builder(this)
                .setTitle("¿Avisar al entrenador?")
                .setMessage("Parece que tienes una molestia o duda urgente. ¿Quieres que envíe un mensaje automático a tu preparador?")
                .setPositiveButton("Sí, enviar aviso", (dialog, which) -> enviarAlertaRealFirebase())
                .setNegativeButton("No por ahora", null)
                .show();
    }

    private void enviarAlertaRealFirebase() {
        // Obtenemos el nombre del entrenador (puedes ajustarlo si tienes el nombre real guardado)
        String nombreEntrenador = "Entrenador"; 
        
        String chatID;
        // Misma lógica de ID que en ChatActivity
        if(miNombre.compareTo(nombreEntrenador) < 0) chatID = miNombre + "_" + nombreEntrenador;
        else chatID = nombreEntrenador + "_" + miNombre;

        Message alerta = new Message();
        alerta.setEmisor(miNombre);
        alerta.setReceptor(nombreEntrenador);
        alerta.setTexto("⚠️ ALERTA: El usuario ha reportado molestias o una duda técnica urgente a la IA durante su entrenamiento.");
        alerta.setTimestamp(System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference("chats").child(chatID).push().setValue(alerta)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Aviso enviado al entrenador correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show());
    }
}
