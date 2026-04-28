package com.example.sportsgo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class TrainerStatsActivity extends AppCompatActivity {

    private TextView tvTotalPupilos;
    private TextView tvTotalEntrenamientos;
    private TextView tvEntrenamientosCompletados;
    private TextView tvPorcentajeCompletado;

    private ListenerRegistration pupilosListener;
    private DatabaseReference entrenamientosRef;
    private ValueEventListener entrenamientosListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_stats);

        tvTotalPupilos = findViewById(R.id.tvTotalPupilos);
        tvTotalEntrenamientos = findViewById(R.id.tvTotalEntrenamientos);
        tvEntrenamientosCompletados = findViewById(R.id.tvEntrenamientosCompletados);
        tvPorcentajeCompletado = findViewById(R.id.tvPorcentajeCompletado);

        MaterialToolbar toolbar = findViewById(R.id.toolbarTrainerStats);
        toolbar.setNavigationOnClickListener(v -> finish());

        escucharPupilos();
        escucharEntrenamientos();
    }

    private void escucharPupilos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance("sports-go-db");
        pupilosListener = db.collection("atletas")
                .addSnapshotListener((snapshot, error) -> {
                    if (snapshot == null) {
                        tvTotalPupilos.setText("0");
                        return;
                    }
                    tvTotalPupilos.setText(String.valueOf(snapshot.size()));
                });
    }

    private void escucharEntrenamientos() {
        entrenamientosRef = FirebaseDatabase.getInstance().getReference("entrenamientos");
        entrenamientosListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int total = 0;
                int completados = 0;

                for (DataSnapshot pupiloNode : snapshot.getChildren()) {
                    for (DataSnapshot entrenamientoNode : pupiloNode.getChildren()) {
                        total++;
                        Boolean completado = entrenamientoNode.child("completado").getValue(Boolean.class);
                        if (Boolean.TRUE.equals(completado)) {
                            completados++;
                        }
                    }
                }

                tvTotalEntrenamientos.setText(String.valueOf(total));
                tvEntrenamientosCompletados.setText(String.valueOf(completados));

                String porcentajeText = total == 0
                        ? "0% completado"
                        : Math.round((completados * 100f) / total) + "% completado";
                tvPorcentajeCompletado.setText(porcentajeText);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                tvTotalEntrenamientos.setText("0");
                tvEntrenamientosCompletados.setText("0");
                tvPorcentajeCompletado.setText("0% completado");
            }
        };
        entrenamientosRef.addValueEventListener(entrenamientosListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pupilosListener != null) {
            pupilosListener.remove();
        }
        if (entrenamientosRef != null && entrenamientosListener != null) {
            entrenamientosRef.removeEventListener(entrenamientosListener);
        }
    }
}

