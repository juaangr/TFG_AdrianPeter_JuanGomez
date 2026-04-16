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
    
    // HISTORIAL DE CONVERSACIÓN PARA QUE TENGA MEMORIA
    private List<Content> chatHistory = new ArrayList<>();
    private final String systemPrompt = "Eres el Asistente de Seguridad de SportsGO. Tu rol es:\n" +
            "1. Resolver dudas sobre TÉCNICA de ejercicios.\n" +
            "2. Si el usuario menciona DOLOR o MOLESTIA, aconseja parar y recomienda informar al entrenador.\n" +
            "3. NO puedes crear rutinas.\n" +
            "4. Si detectas un problema de salud o dolor, termina tu respuesta con la frase exacta: '[ALERTA_ENTRENADOR]'.\n" +
            "Sé profesional y breve.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemini_chat);

        SharedPreferences prefs = getSharedPreferences("PrefeSportsGO", MODE_PRIVATE);
        miNombre = prefs.getString("user_nombre", "Usuario");

        lvChat = findViewById(R.id.lvChatGemini);
        etMensaje = findViewById(R.id.etMensajeGemini);
        btnEnviar = findViewById(R.id.btnEnviarGemini);

        chatMessages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatMessages);
        lvChat.setAdapter(adapter);

        // CONFIGURACIÓN DE GEMINI
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                com.example.sportsgo.BuildConfig.GEMINI_API_KEY
        );
        model = GenerativeModelFutures.from(gm);

        // Añadimos el mensaje inicial al historial visual y a la IA
        chatMessages.add("IA: ¡Hola! Soy tu asistente de seguridad. ¿En qué puedo ayudarte hoy?");
        adapter.notifyDataSetChanged();

        btnEnviar.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String query = etMensaje.getText().toString().trim();
        if (query.isEmpty()) return;

        // 1. Mostrar en UI
        chatMessages.add("Tú: " + query);
        adapter.notifyDataSetChanged();
        etMensaje.setText("");
        lvChat.setSelection(adapter.getCount() - 1);

        // 2. Construir el contexto del mensaje (System Prompt + Historial + Nueva Pregunta)
        Content userContent = new Content.Builder()
                .addText(query)
                .build();
        
        // Creamos la petición enviando el sistema de instrucciones y el historial
        // En una implementación más compleja usaríamos model.startChat(), 
        // pero para este nivel, concatenar el contexto es muy efectivo y fácil de explicar.
        
        StringBuilder fullPrompt = new StringBuilder(systemPrompt + "\n\nHistorial:\n");
        for (Content c : chatHistory) {
            fullPrompt.append(c.getRole()).append(": ").append(c.getParts().get(0).toString()).append("\n");
        }
        fullPrompt.append("Usuario: ").append(query);

        Content finalRequest = new Content.Builder()
                .addText(fullPrompt.toString())
                .build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(finalRequest);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                runOnUiThread(() -> {
                    // Guardar en el historial de memoria
                    chatHistory.add(userContent);
                    chatHistory.add(new Content.Builder().addText(resultText).build());

                    if (resultText != null && resultText.contains("[ALERTA_ENTRENADOR]")) {
                        String cleanMsg = resultText.replace("[ALERTA_ENTRENADOR]", "").trim();
                        chatMessages.add("IA: " + cleanMsg);
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
                    Toast.makeText(GeminiChatActivity.this, "Error de IA: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }, executor);
    }

    private void mostrarDialogoAlertaEntrenador() {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Aviso de Seguridad")
                .setMessage("¿Deseas enviar una alerta inmediata a tu entrenador sobre este dolor/duda?")
                .setPositiveButton("Sí, enviar aviso", (dialog, which) -> enviarAlertaRealFirebase())
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void enviarAlertaRealFirebase() {
        String nombreEntrenador = "Entrenador"; 
        String chatID = (miNombre.compareTo(nombreEntrenador) < 0) ? 
                miNombre + "_" + nombreEntrenador : nombreEntrenador + "_" + miNombre;

        Message alerta = new Message();
        alerta.setEmisor(miNombre);
        alerta.setReceptor(nombreEntrenador);
        alerta.setTexto("⚠️ ALERTA: El usuario ha reportado molestias o una duda técnica urgente a través de la IA.");
        alerta.setTimestamp(System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference("chats").child(chatID).push().setValue(alerta)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Aviso enviado al entrenador", Toast.LENGTH_SHORT).show());
    }
}
