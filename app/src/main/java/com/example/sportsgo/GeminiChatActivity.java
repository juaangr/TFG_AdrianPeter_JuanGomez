package com.example.sportsgo;

import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemini_chat);

        lvChat = findViewById(R.id.lvChatGemini);
        etMensaje = findViewById(R.id.etMensajeGemini);
        btnEnviar = findViewById(R.id.btnEnviarGemini);

        chatMessages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatMessages);
        lvChat.setAdapter(adapter);

        // CONFIGURACIÓN DE GEMINI
        // IMPORTANTE: Reemplaza "TU_API_KEY" con una clave válida de Google AI Studio
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                "TU_API_KEY"
        );
        model = GenerativeModelFutures.from(gm);

        // Mensaje de bienvenida de la IA
        chatMessages.add("IA: ¡Hola! Soy tu asistente de SportsGO. Pregúntame cualquier duda sobre tus ejercicios o si sientes alguna molestia. Recuerda que no puedo cambiar tu rutina, eso es cosa de tu entrenador.");
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

        // System Prompt para que la IA sepa su rol
        String systemPrompt = "Eres un asistente virtual de gimnasio para una app llamada SportsGO. " +
                "Tu objetivo es ayudar al pupilo con dudas inmediatas sobre la ejecución de ejercicios, " +
                "dolores leves, o consejos motivacionales. " +
                "IMPORTANTE: No puedes modificar la rutina asignada por el entrenador. " +
                "Si el usuario pide cambiar ejercicios, indícale que debe consultarlo con su entrenador. " +
                "Sé conciso y profesional.";

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
                    chatMessages.add("IA: " + resultText);
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
}