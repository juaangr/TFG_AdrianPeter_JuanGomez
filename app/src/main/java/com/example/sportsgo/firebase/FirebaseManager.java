package com.example.sportsgo.firebase;

import com.example.sportsgo.entities.Admin;
import com.example.sportsgo.entities.Trainer;
import com.example.sportsgo.entities.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/*
* ESTA CLASE HABRÁ QUE REFACTORIZARLA MUCHO (JUAN)
* */
public class FirebaseManager {

    private static FirebaseManager instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    private static final String COL_USERS    = "atletas";
    private static final String COL_TRAINERS = "trainers";
    private static final String COL_ADMINS   = "admins";

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance("sports-go-db");
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // ── REGISTRO USER ─────────────────────────────────────────
    public void registerUser(String nombre, String email, String password,
                             OnSuccessListener<Void> onSuccess,
                             OnFailureListener onFailure) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    User user = new User(uid, nombre, email);

                    db.collection(COL_USERS)
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(onSuccess)
                            .addOnFailureListener(onFailure);
                })
                .addOnFailureListener(onFailure);
    }

    // ── REGISTRO TRAINER ──────────────────────────────────────
    public void registerTrainer(String nombre, String email, String password,
                                OnSuccessListener<Void> onSuccess,
                                OnFailureListener onFailure) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    Trainer trainer = new Trainer(uid, nombre, email);

                    db.collection(COL_TRAINERS)
                            .document(uid)
                            .set(trainer)
                            .addOnSuccessListener(onSuccess)
                            .addOnFailureListener(onFailure);
                })
                .addOnFailureListener(onFailure);
    }

    // ── REGISTRO ADMIN ────────────────────────────────────────
    public void registerAdmin(String nombre, String email, String password,
                              OnSuccessListener<Void> onSuccess,
                              OnFailureListener onFailure) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    Admin admin = new Admin(uid, nombre, email);

                    db.collection(COL_ADMINS)
                            .document(uid)
                            .set(admin)
                            .addOnSuccessListener(onSuccess)
                            .addOnFailureListener(onFailure);
                })
                .addOnFailureListener(onFailure);
    }

    // ── LOGIN (común para todos) ──────────────────────────────
    public void login(String email, String password,
                      OnSuccessListener<AuthResult> onSuccess,
                      OnFailureListener onFailure) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // ── LOGOUT ────────────────────────────────────────────────
    public void logout() {
        auth.signOut();
    }

    // ── USUARIO ACTUAL ────────────────────────────────────────
    public FirebaseUser getUsuarioActual() {
        return auth.getCurrentUser();
    }

    // ── DETECTAR ROL TRAS LOGIN ───────────────────────────────
    public void getRol(String uid, OnSuccessListener<String> onSuccess,
                       OnFailureListener onFailure) {

        db.collection(COL_USERS).document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) { onSuccess.onSuccess("atletas"); return; }

                    db.collection(COL_TRAINERS).document(uid).get()
                            .addOnSuccessListener(doc2 -> {
                                if (doc2.exists()) { onSuccess.onSuccess("trainers"); return; }

                                db.collection(COL_ADMINS).document(uid).get()
                                        .addOnSuccessListener(doc3 -> {
                                            if (doc3.exists()) onSuccess.onSuccess("admins");
                                        })
                                        .addOnFailureListener(onFailure);
                            })
                            .addOnFailureListener(onFailure);
                })
                .addOnFailureListener(onFailure);
    }

    // ── CALORIAS DEL ATLETA ───────────────────────────────────
    public void getAtletaCalorias(String uid, OnSuccessListener<Integer> onSuccess,
                                  OnFailureListener onFailure) {
        db.collection(COL_USERS).document(uid).get()
                .addOnSuccessListener(doc -> {
                    Long calorias = doc.getLong("calorias");
                    onSuccess.onSuccess(calorias != null ? calorias.intValue() : 0);
                })
                .addOnFailureListener(onFailure);
    }

    public void updateAtletaCalorias(String uid, int calorias, boolean permisoCompleto,
                                     OnSuccessListener<Void> onSuccess,
                                     OnFailureListener onFailure) {
        db.collection(COL_USERS).document(uid)
                .update("calorias", calorias, "permisoCompleto", permisoCompleto)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
}